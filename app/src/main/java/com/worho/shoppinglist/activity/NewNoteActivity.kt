package com.worho.shoppinglist.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.worho.shoppinglist.R
import com.worho.shoppinglist.databinding.ActivityNewNoteBinding
import com.worho.shoppinglist.entities.NoteItem
import com.worho.shoppinglist.fragments.NoteFragment
import com.worho.shoppinglist.utils.HtmlManager
import com.worho.shoppinglist.utils.MyTouchListener
import com.worho.shoppinglist.utils.TimeManager

class NewNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewNoteBinding
    //для проверки
    private var note: NoteItem? = null
    private var preference: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        preference = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedThem())
        setContentView(binding.root)
        actionBarSettings()
        getNote()
        init()
        setTextSize()
        onClickColorPicker()
        actionMenuCallback()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(){
        binding.collerPicker.setOnTouchListener(MyTouchListener())
        preference = PreferenceManager.getDefaultSharedPreferences(this)
    }

    private fun onClickColorPicker() = with(binding) {
        imBlack.setOnClickListener {
            setColorSelectedText(R.color.picker_black)
        }
        imBlue.setOnClickListener {
            setColorSelectedText(R.color.picker_blue)
        }
        imGreen.setOnClickListener {
            setColorSelectedText(R.color.picker_green)
        }
        imRed.setOnClickListener {
            setColorSelectedText(R.color.picker_red)
        }
        imOrange.setOnClickListener {
            setColorSelectedText(R.color.picker_orange)
        }
        imYelow.setOnClickListener {
            setColorSelectedText(R.color.picker_yellow)
        }

    }

    private fun getNote(){
        var sNote = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY)
        if (sNote != null) {
            note = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY) as NoteItem
            fillNote()
        }
    }

    private fun fillNote() = with(binding){
            etTitle.setText(note?.title)
        etDescription.setText(HtmlManager.getFromHtml(note?.content!!).trim())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.new_note_menu_save -> setMainResult()
            android.R.id.home -> finish()
            R.id.new_note_menu_bold -> setBoldSelectedText()
            R.id.new_note_menu_color -> openColorPicker()
        }
        return super.onOptionsItemSelected(item)
    }
    // выделение текста доделать полностью
    private fun setBoldSelectedText() = with(binding){
        val startPos = etDescription.selectionStart
        val endPos = etDescription.selectionEnd
        //спец функция
        val styles =  etDescription.text.getSpans(startPos, endPos, StyleSpan::class.java)
        var boldStyle : StyleSpan? = null
        if (styles.isNotEmpty()){
            etDescription.text.removeSpan(styles[0])
        } else {
            boldStyle = StyleSpan(Typeface.BOLD)
            etDescription.text.setSpan(boldStyle, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            etDescription.text.trim()
            etDescription.setSelection(endPos)
        }
    }

    // доделать полностью
    private fun setColorSelectedText(colorId : Int) = with(binding){
        val startPos = etDescription.selectionStart
        val endPos = etDescription.selectionEnd
        //спец функция
        val styles =  etDescription.text.getSpans(startPos, endPos, ForegroundColorSpan::class.java)
        if (styles.isNotEmpty()){
            etDescription.text.removeSpan(styles[0])
        }

            etDescription.text.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(this@NewNoteActivity, colorId))
                , startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            etDescription.text.trim()
            etDescription.setSelection(endPos)
    }

    private fun setMainResult(){
        var editState = "new"
        val tempNote: NoteItem? = if (note == null){
            createNewNote()
        } else {
            editState = "update"
            updateNote()
        }
        val intent = Intent().apply {
            putExtra(NoteFragment.NEW_NOTE_KEY, tempNote)
            putExtra(NoteFragment.EDIT_STATE_KEY, editState)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun updateNote(): NoteItem? = with(binding){
        return note?.copy(
            title = etTitle.text.toString(),
            content = HtmlManager.toText(etDescription.text)
        )
    }

    private fun createNewNote(): NoteItem {
        return NoteItem(
            null,
            binding.etTitle.text.toString(),
            HtmlManager.toText(binding.etDescription.text),
            TimeManager.getCurrentTime(),
            ""
        )
    }



    private fun actionBarSettings(){
        val ab= supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)

    }

    private fun openColorPicker(){
        if (binding.collerPicker.isShown){
            closeColorPicker()
        } else {
            binding.collerPicker.visibility = View.VISIBLE
            val openAnim = AnimationUtils.loadAnimation(this, R.anim.open_color_picker)
            binding.collerPicker.startAnimation(openAnim)
        }
    }

    private fun closeColorPicker(){
        val closeAnim = AnimationUtils.loadAnimation(this , R.anim.close_color_picker)
        closeAnim.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                binding.collerPicker.visibility = View.GONE
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })
        binding.collerPicker.startAnimation(closeAnim)

    }
    private fun actionMenuCallback(){
        val actionCallback = object : ActionMode.Callback{
            override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
                p1?.clear()
                return true
            }

            override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
                p1?.clear()
                return true
            }

            override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
                return true
            }

            override fun onDestroyActionMode(p0: ActionMode?) {
            }

        }
        binding.etDescription.customSelectionActionModeCallback = actionCallback
    }

    private fun setTextSize() = with(binding){
        etTitle.setTextSize(preference?.getString("title_text_size_key", "16"))
        etDescription.setTextSize(preference?.getString("content_text_size_key", "14"))

    }
    private fun getSelectedThem(): Int{
        return when(preference?.getString("chose_theme_key", "blue")){
            "blue" -> R.style.Theme_NewNoteBlue
            "green" -> R.style.Theme_NewNoteGreen
            else -> R.style.Theme_NewNoteBlue
        }
    }

    private fun EditText.setTextSize(size: String?){
        if (size != null) this.textSize = size.toFloat()
    }
}