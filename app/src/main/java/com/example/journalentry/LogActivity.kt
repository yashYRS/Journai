package com.example.journalentry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.journalentry.database.DatabaseManager
import com.example.journalentry.database.Entity
import com.example.journalentry.database.Note
import com.example.journalentry.getapi.ApiManager
import com.example.journalentry.getapi.Post
import com.example.journalentry.recycleradapter.Model
import com.example.journalentry.recycleradapter.RvAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogActivity: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.logs, container, false)
        val searchBarText: EditText = view.findViewById(R.id.search_text)
        val searchBarSubmit: Button = view.findViewById(R.id.search_submit_button)
        val searchBarCancel: Button = view.findViewById(R.id.search_cancel_button)
        val loading: ProgressBar = view.findViewById(R.id.progressBar)
        loading.visibility = View.INVISIBLE
        val card = Cards(view, activity as AppCompatActivity)
        card.updateCards()

        searchBarCancel.setOnClickListener {
            if (searchBarText.text.isNotEmpty()) {
                searchBarText.text.clear()
            }
        }

        searchBarSubmit.setOnClickListener {
            val query = searchBarText.text.toString()
            if (query.isNotEmpty()) {
                card.clearCards()
                card.updateCardsWithQuery(query, loading)
            }
        }

        return view
    }

    class Cards(private val view: View, private val activity: AppCompatActivity) {
        private val cardManager = LinearLayoutManager(view.context, LinearLayout.VERTICAL, false)

        fun updateCards() {
            val db = DatabaseManager(activity)
            val recyclerView = view.findViewById<RecyclerView>(R.id.card_view)
            recyclerView.layoutManager = cardManager
            val notes = db.getNotes()
            val list = ArrayList<Model>()
            for (n in notes) {
                val id = db.getNoteId(n)
                list.add(Model(id.toString(),n.content))
            }
            db.close()
            val rvAdapter = RvAdapter(list)
            recyclerView.adapter = rvAdapter
        }

        fun clearCards() {
            val recyclerView = view.findViewById<RecyclerView>(R.id.card_view)
            recyclerView.adapter = null
        }

        fun updateCardsWithQuery(query: String, loading: ProgressBar) {
            val db = DatabaseManager(activity)
            val call = ApiManager().getQuery(query)
            loading.visibility = View.VISIBLE
            call!!.enqueue(object : Callback<Post?> {
                override fun onResponse(call: Call<Post?>, response: Response<Post?>) {
                    if (!response.isSuccessful) {
                        println("Code: " + response.code())
                        return
                    }
                    loading.visibility = View.INVISIBLE
                    val witResolvedQuery = response.body()?.witEntities
                    val queryEntities = ArrayList<Entity> ()
                    if (witResolvedQuery != null) {
                        for ((_,value) in witResolvedQuery) {
                            val entityVal = value[0]["value"].toString()
                            val entityTyp = value[0]["type"].toString()
                            val entityScore = value[0]["confidence"].toString().toFloat()
                            val entityKey = value[0]["body"].toString()
                            val sentId = 0
                            queryEntities.add(Entity(entityKey, entityVal, sentId, entityTyp, entityScore))
                        }
                    }
                    val notes = fuzzySearch(queryEntities)
                    val recyclerView = view.findViewById<RecyclerView>(R.id.card_view)
                    recyclerView.layoutManager = cardManager
                    val list = ArrayList<Model>()
                    for (n in notes) {
                        val id = db.getNoteId(n)
                        list.add(Model(id.toString(),n.content))
                    }
                    db.close()
                    val rvAdapter = RvAdapter(list)
                    recyclerView.adapter = rvAdapter

                }

                override fun onFailure(call: Call<Post?>, t: Throwable) {
                    println(t.message)
                }
            })

        }

        private fun getScores(queryEntities: MutableMap<String,String>, queryScores: MutableMap<String,String>, sentenceEntities: MutableMap<String,String>, sentenceScores: MutableMap<String,String>): Float {
            var score = 0f
            for ((_, queryVal) in queryEntities) {
                for ((sentKey, sentVal) in sentenceEntities) {
                    // Exact Match
                    if (queryVal == sentVal) {
                        score += sentenceScores[sentKey]!!.toFloat() * queryScores[sentKey]!!.toFloat()
                        break
                    }
                }

                for ((key, _) in sentenceEntities) {
                    // Resolved entity is same, weightage is half
                    if (sentenceEntities[key] in queryEntities) {
                        score += sentenceScores[key]!!.toFloat() * queryScores[key]!!.toFloat()
                    }
                }
            }
            return score
        }

        private fun updateEntities(msgId: Int): ArrayList<MutableMap<String,String>> {
            val db = DatabaseManager(activity)
            val entities = db.getEntities(msgId)
            db.close()
            // Get the entity after resolving from the db
            val phraseMap = mutableMapOf<String,String>()

            // Get the confidence of the previous map
            val scoreMap = mutableMapOf<String,String>()

            val mapList = ArrayList<MutableMap<String, String>> ()
            for (entity in entities) {
                phraseMap[entity.key] = entity.value
                scoreMap[entity.key] = entity.score.toString()
            }
            mapList.add(phraseMap)
            mapList.add(scoreMap)
            return mapList
        }

        private fun fuzzySearch(queryMsg: ArrayList<Entity>): ArrayList<Note> {
            val db = DatabaseManager(activity)
            val notes = ArrayList<Note>()
            // get entity dictionaries for query msg
            val queryEntities =  mutableMapOf<String,String>()
            val queryScores = mutableMapOf<String,String>()
            for (value in queryMsg) {
                queryEntities[value.key] = value.value
                queryScores[value.key] = value.score.toString()
            }
            // temporary variable creation
            var currScore: Float

            // Dictionary to keep track of scores
            val noteScore = mutableMapOf("1" to 0f)
            val sentenceList = db.getSentences()
            // Go over all sentences
            for (sentence in sentenceList) {
                // get the dictionaries per sentence
                val sentenceEntities = updateEntities(db.getSentenceId(sentence.content))
                // calculate similarity
                currScore = getScores(queryEntities, queryScores, sentenceEntities[0], sentenceEntities[1])
                // check if note id put in noteScore dictionary
                if (sentence.noteId.toString() !in noteScore.keys) {
                    noteScore[sentence.noteId.toString()] = 0f
                }
                noteScore[sentence.noteId.toString()] = noteScore[sentence.noteId.toString()]!!.plus(currScore)
            }
            val sortedMap = noteScore.toList()
                    .sortedBy { (_, value) -> value }
                    .toMap()
            for((key,_) in sortedMap){
                val note = db.getNote(key)
                notes.add(note)
            }
            db.close()
            return notes
        }
    }
}

