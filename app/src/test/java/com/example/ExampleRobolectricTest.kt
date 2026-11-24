package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.GameScoreRecord
import com.example.data.StudioDatabase
import com.example.data.StudioMessage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: StudioDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, StudioDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun read_string_from_context() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("NovaHub", appName)
    }

    @Test
    fun test_studio_message_insert_and_retrieve() = runBlocking {
        val dao = db.studioDao()
        val message = StudioMessage(
            senderName = "Dev Collaborator",
            senderEmail = "test@example.com",
            category = "Collaboration",
            message = "Excited about your browser game projects."
        )
        dao.insertMessage(message)

        val messages = dao.getAllMessages().first()
        assertEquals(1, messages.size)
        assertEquals("Dev Collaborator", messages[0].senderName)
        assertEquals("Collaboration", messages[0].category)
    }

    @Test
    fun test_game_score_record() = runBlocking {
        val dao = db.studioDao()
        dao.insertGameScore(
            GameScoreRecord(
                gameId = "void_runner",
                score = 350,
                shardsCollected = 3
            )
        )
        val highest = dao.getHighestScore("void_runner").first()
        assertEquals(350, highest)
    }
}
