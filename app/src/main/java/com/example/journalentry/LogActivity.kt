package com.example.journalentry

import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate
import com.example.journalentry.database.DatabaseManager
import com.example.journalentry.database.Note
import com.example.journalentry.getapi.ApiManager
import com.example.journalentry.recycleradapter.Model
import com.example.journalentry.recycleradapter.RvAdapter
import kotlinx.android.synthetic.main.logs.*

class LogActivity: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.logs, container, false)
        val searchBar: SearchView = view.findViewById(R.id.search)
        val card = Cards(view, activity as AppCompatActivity)
        card.updateCards()

        searchBar.setOnSearchClickListener {
            val query = searchBar.query.toString()
//            updateCardsWithQuery(query)
        }
        return view
    }

//    private fun updateCardsWithQuery(query: String) {
////        val notes = fuzzySearch(query)
//        val recyclerView = view.findViewById<RecyclerView>(R.id.card_view)
//        recyclerView.layoutManager = cardManager
//        val list = ArrayList<Model>()
//        for (n in notes) {
//            val id = db.getNoteId(n)
//            db.close()
//            list.add(Model(id.toString(),n.content))
//        }
//        val rvAdapter = RvAdapter(list)
//        recyclerView.adapter = rvAdapter
//    }

//    fun getScores(query_entities, query_scores, sentence_scores, sentence_entities): Int{
//        val score = 0
//        for (word_query in query_entities) {
//
//            for (word_sentence in sentence_entities) {
//                // Exact Match
//                if (word_query == word_sentence) {
//                    score += sentence_scores[word_sentence] * query_scores[word_query]
//                    break
//                }
//            }
//
//            for (word_sentence in sentence_entities) {
//                // Resolved entity is same, weightage is half
//                if (sentence_entities[word_sentence] == query_entities[word_query]) {
//                    score += sentence_scores[word_sentence] * query_scores[word_query]
//                }
//            }
//        }
//        return score
//    }
//
//    fun getEntities(msg: Note): List<MutableMap<String, Float>> {
//
//        // Get the entity after resolving from the db
//        val phraseMap = mutableMapOf("actual_phrase" to "resolved_entity")
//
//        // Get the confidence of the previous map
//        val scoreMap = mutableMapOf("actual_phrase" to "confidence")
//
//        for (entities in msg) {
//            phraseMap["actual_phrase"] = resolved_entity
//            scoreMap["actual_phrase"] = confidence
//        }
//        return List(phraseMap, scoreMap)
//    }
//
//    fun fuzzySearch(query_msg: String) {
//        val post = ApiManager.getQuery(query_msg)
//        // get entity dictionaries for query msg
//        queryEntities, query_scores = get_entities(query_msg)
//        // temporary variable creation
//        var curr_score = 0
//
//        // Dictionary to keep track of scores
//        val noteScore = mutableMapOf("1" to 0)
//
//        // Go over all sentences
//        for (sentence in sentenceList) {
//            // get the dictionaries per sentence
//            sentenceScores, sentenceEntities = get_entities(sentence)
//            // calculate similarity
//            curr_score = getScores(query_entities, query_scores, sentence_scores, sentence_entities)
//            // check if noteid put in noteScore dictionary
//            if (sentence ["noteid"] !in noteScore) {
//                noteScore["noteid"] = 0
//            }
//            sentence["noteid"] = sentence["noteid"] + curr_score
//        }
//    }


    class Cards(private val view: View, activity: AppCompatActivity) {
        private val db = DatabaseManager(activity)
        private val cardManager = LinearLayoutManager(view.context, LinearLayout.VERTICAL, false)

        fun updateCards() {
            val recyclerView = view.findViewById<RecyclerView>(R.id.card_view)
            recyclerView.layoutManager = cardManager
            val notes = db.getNotes()
            val list = ArrayList<Model>()
            for (n in notes) {
                val id = db.getNoteId(n)
                db.close()
                list.add(Model(id.toString(),n.content))
            }
            val rvAdapter = RvAdapter(list)
            recyclerView.adapter = rvAdapter
        }
    }
}

