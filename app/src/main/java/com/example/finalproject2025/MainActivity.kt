package com.example.finalproject2025

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.util.Scanner

data class Item(val name: String, val description: String)
data class Event(val markerNum: String, val scenario: String, val prompts: List<String>, val nextEvents: List<String>)

class MainActivity : AppCompatActivity() {
    private val ADD_CHARACTER_NAME_CODE: Int = 2202
    private var characterName : String = ""
    private var currentMarker : String = ""
    private var leftButtonPrompt: String = ""
    private var forwardButtonPrompt: String = ""
    private var rightButtonPrompt: String = ""
    private var inventory = mutableListOf<Item>()
    private var allItems = mutableListOf<Item>()
    private var storyline = mutableListOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startGame()
    }

    private fun startGame()
    {
        loadPlayerData()
        loadEvents()
        updateMainScreen()
    }

    private fun loadPlayerData()
    {
        characterName = intent.getStringExtra("name")?:"UNABLE TO RETRIEVE"
        currentMarker = intent.getStringExtra("storyMarker")?:"UNABLE TO RETRIEVE"

        findViewById<TextView>(R.id.selected_player_name).text = "Name: " + characterName
        findViewById<TextView>(R.id.current_story_marker).text = "Event: " + currentMarker
    }

    private fun loadEvents()
    {
        val reader = Scanner(resources.openRawResource(R.raw.events))

        while(reader.hasNextLine())
        {
            val line = reader.nextLine()
            val eventData = line.split("|")

            val marker = eventData[0]
            val scenario = eventData[1]
            val eventPrompts = eventData[2]
            val nextEventMarkers = eventData[3]

            val prompts = eventPrompts.split(",")
            val nextMarkers = nextEventMarkers.split(",")

            storyline.add(Event(marker, scenario, prompts, nextMarkers))
        }
    }

    private fun updateMainScreen()
    {
        val currentEvent = findCurrentEvent()

        resetButtons()

        setPrompts(currentEvent)

        checkNoneButtons()

        if(currentMarker == "-1")
        {
            finish()
        }

        else
        {
            findViewById<TextView>(R.id.scenario_textview).text = currentEvent.scenario
            findViewById<TextView>(R.id.left_button_option).text = currentEvent.prompts[0]
            findViewById<TextView>(R.id.forward_button_option).text = currentEvent.prompts[1]
            findViewById<TextView>(R.id.right_button_option).text = currentEvent.prompts[2]
            findViewById<TextView>(R.id.save_and_quit_button).text = "Save and Quit"
        }
    }

    private fun resetButtons()
    {
        findViewById<TextView>(R.id.left_button_option).visibility = View.VISIBLE
        findViewById<TextView>(R.id.forward_button_option).visibility = View.VISIBLE
        findViewById<TextView>(R.id.right_button_option).visibility = View.VISIBLE
    }

    private fun setPrompts(currentEvent: Event)
    {
        leftButtonPrompt = currentEvent.prompts[0]
        forwardButtonPrompt = currentEvent.prompts[1]
        rightButtonPrompt = currentEvent.prompts[2]
    }

    private fun checkNoneButtons()
    {
        if(leftButtonPrompt == "NONE")
        {
            findViewById<TextView>(R.id.left_button_option).visibility = View.INVISIBLE
        }

        if(forwardButtonPrompt == "NONE")
        {
            findViewById<TextView>(R.id.forward_button_option).visibility = View.INVISIBLE
        }

        if(rightButtonPrompt == "NONE")
        {
            findViewById<TextView>(R.id.right_button_option).visibility = View.INVISIBLE
        }
    }

    private fun findCurrentEvent() : Event
    {
        var currentEvent = Event("", "", listOf(), listOf())

        for(evt in storyline)
        {
            if(evt.markerNum == currentMarker)
            {
                currentEvent = evt
            }
        }

        return currentEvent
    }

    private fun findNextEventMarker(nextMarker: String)
    {
        var currentEvent = findCurrentEvent()

        for(evt in storyline)
        {
            if(evt.markerNum == nextMarker)
            {
                currentEvent = evt
            }
        }

        currentMarker = currentEvent.markerNum
    }

    fun leftButton (view: View)
    {
        val currentEvent = findCurrentEvent()
        val chosenOption = currentEvent.nextEvents[0]

        findNextEventMarker(chosenOption)

        findViewById<TextView>(R.id.current_story_marker).text = "Event: " + currentMarker

        updateMainScreen()

    }

    fun forwardButton (view: View)
    {
        val currentEvent = findCurrentEvent()
        val chosenOption = currentEvent.nextEvents[1]

        findNextEventMarker(chosenOption)

        findViewById<TextView>(R.id.current_story_marker).text = "Event: " + currentMarker

        updateMainScreen()
    }

    fun rightButton (view: View)
    {
        val currentEvent = findCurrentEvent()
        val chosenOption = currentEvent.nextEvents[2]

        findNextEventMarker(chosenOption)

        findViewById<TextView>(R.id.current_story_marker).text = "Event: " + currentMarker

        updateMainScreen()
    }

    fun bottomButton(view: View)
    {
        val file = File(application.filesDir, "user_data.csv")

        file.writeText("${characterName}|${currentMarker}")

        finish()
    }
}