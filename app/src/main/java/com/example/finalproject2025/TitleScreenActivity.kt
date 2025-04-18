package com.example.finalproject2025

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Scanner
import java.io.File
import java.io.FileInputStream

class TitleScreenActivity : AppCompatActivity() {
    private val CHANGE_NAME_CODE: Int = 2202
    private var characterName: String  = "Daniel"
    private var storyMarker: String = "START"
    private var inventory = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_title_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CHANGE_NAME_CODE && resultCode == RESULT_OK && data != null)
        {
            characterName = data.getStringExtra("name")?:"UNABLE TO RETRIEVE"

            findViewById<TextView>(R.id.player_name_viewer).text = "Player Name: " + characterName

        }
    }

    fun startNewGame(view : View)
    {
        val myIntent = Intent(this, MainActivity::class.java)
        val file = File(application.filesDir, "user_data.csv")

        storyMarker = "START"

        if(file.exists())
        {
            file.writeText("")
        }

        myIntent.putExtra("name", characterName)
        myIntent.putExtra("storyMarker", storyMarker)

        startActivity(myIntent)
    }

    fun loadGame(view : View)
    {
        val myIntent = Intent(this, MainActivity::class.java)
        val file = File(application.filesDir, "user_data.csv")

        val savedData = checkAndReadFile(file, myIntent)

        if(savedData == true)
        {
            startActivity(myIntent)
        }

        else
        {
            Toast.makeText(this, "No Saved Data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAndReadFile(file: File, myIntent: Intent): Boolean
    {
        var savedDataExists = false

        if (file.exists()) {
            val readResult = FileInputStream(file)
            val scanner = Scanner(readResult)

            while(scanner.hasNextLine()){
                val line = scanner.nextLine()
                val playerData = line.split("|")

                characterName = playerData[0]
                storyMarker = playerData[1]

                myIntent.putExtra("name", characterName)
                myIntent.putExtra("storyMarker", storyMarker)

                savedDataExists = true
            }
        } else { // default data

            file.createNewFile()

        }

        return savedDataExists
    }

    fun changeName(view : View)
    {
        val myIntent = Intent(this, EnterNameActivity::class.java)
        startActivityForResult(myIntent, CHANGE_NAME_CODE)
    }


}