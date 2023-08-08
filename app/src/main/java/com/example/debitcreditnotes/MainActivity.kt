package com.example.debitcreditnotes

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.sqlkt.Sql.DbHelper
import com.example.sqlkt.Sql.Notes
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var list: ListView
    lateinit var NotesList: List<Notes>
    lateinit var credit: TextView
    lateinit var debit: TextView
    lateinit var balance: TextView
    lateinit var add: CardView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list = findViewById(R.id.list)
        credit = findViewById(R.id.CreditAmount)
        debit = findViewById(R.id.DebitAmount)
        balance = findViewById(R.id.BalaneceAmount)
        add = findViewById(R.id.addcard)

        add.setOnClickListener {
            showCustomDialog()
        }





        refresh()
    }

    fun refresh() {
        val db = DbHelper(this)
        credit.text = db.getTotalCreditAmount().toString()
        debit.text = db.getTotalDebitAmount().toString()
        balance.text = "Total Balance : "+(db.getTotalDebitAmount() - db.getTotalCreditAmount()).toString()
        NotesList = db.getNotes()
        val adapter = MyAdapter(this, NotesList)
        list.adapter = adapter
    }


    private fun showCustomDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_data, null)

        val editTextAmount = dialogView.findViewById<EditText>(R.id.editTextAmount)
        val editTextDate = dialogView.findViewById<TextView>(R.id.editTextDate)
        val editTextDes = dialogView.findViewById<EditText>(R.id.editTextDescrition)
        val SpinerCD = dialogView.findViewById<Spinner>(R.id.spinner)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnAdd)

        editTextDate.text=SimpleDateFormat("dd-MM-yyyy").format(System.currentTimeMillis())


        val items = arrayOf( "Debit","Credit")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        SpinerCD.setAdapter(adapter)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
        val alertDialog = dialogBuilder.create()
        btnAdd.setOnClickListener {
            val amount = editTextAmount.text.toString()
            val date = editTextDate.text.toString()
            val des = editTextDes.text.toString()
            val db = DbHelper(this)
            if (items[SpinerCD.selectedItemPosition] == "Credit") {
                db.insertCreditNote(amount.toDouble(), date, des)
                alertDialog.dismiss()
                refresh()
            } else if (items[SpinerCD.selectedItemPosition] == "Debit") {
                db.insertDebitNote(amount.toDouble(), date, des)
                alertDialog.dismiss()
                refresh()
            }
        }
        alertDialog.show()
    }



}



class MyAdapter(val context: Context, val list:List<Notes>): BaseAdapter(){
    override fun getCount(): Int {  return list.size }

    override fun getItem(p0: Int): Any { return list[p0] }

    override fun getItemId(p0: Int): Long {return p0.toLong()}

    override fun getView(i: Int, view: View?, parent: ViewGroup?): View {
        val view= LayoutInflater.from(context).inflate(R.layout.item,parent,false)
        val txt1=view.findViewById<TextView>(R.id.date)
        val txt2=view.findViewById<TextView>(R.id.cd)
        val txt3=view.findViewById<TextView>(R.id.amount)
        val txt4=view.findViewById<TextView>(R.id.des)
        txt1.text=list[i].Date
        if(list[i].Is_Debit) txt2.text="Debit" else txt2.text="Credit"
        txt3.text="Amount : "+list[i].Amount
        txt4.text=list[i].Description
        return view
    }

}
