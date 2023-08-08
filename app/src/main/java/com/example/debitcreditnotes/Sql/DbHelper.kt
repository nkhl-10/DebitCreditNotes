package com.example.sqlkt.Sql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Notes(
    val ID:Int,
    val Amount:String,
    val Date:String,
    val Description:String,
    val Is_Debit:Boolean
)

class DbHelper(context: Context):SQLiteOpenHelper(context,DBNAME,null,VERSION) {

   companion object {
       private const val VERSION=1
       private const val DBNAME="DebitCredit"
       private const val TNAME = "Notes"
       private const val ID = "id"
       private const val AMOUNT = "amount"
       private const val DATE = "date"
       private const val DESCRIPTION = "description"
       private const val IS_DEBIT = "is_debit"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val sql="CREATE TABLE $TNAME ($ID INTEGER PRIMARY KEY AUTOINCREMENT ,$AMOUNT REAL NOT NULL,  $DATE TEXT NOT NULL, $DESCRIPTION  TEXT,  $IS_DEBIT INTEGER NOT NULL)"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TNAME")
        onCreate(db)
    }

    fun insertDebitNote(amount: Double, date: String, description: String?) {
        insertNote(amount, date, description, true)
    }

    fun insertCreditNote(amount: Double, date: String, description: String?) {
        insertNote(amount, date, description, false)
    }
    fun insertNote(amount: Double, date: String, description: String?, isDebit: Boolean) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(AMOUNT, amount)
        cv.put(DATE, date)
        cv.put(DESCRIPTION, description)
        cv.put(IS_DEBIT,if (isDebit) 1 else 0)
        db.insert(TNAME, null, cv)
        db.close()
    }


    fun getTotalDebitAmount(): Double {
        return getTotalAmount(true)
    }

    fun getTotalCreditAmount(): Double {
        return getTotalAmount(false)
    }

    private fun getTotalAmount(isDebit: Boolean): Double {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM(amount) FROM notes WHERE is_debit = ?",
            arrayOf(if (isDebit) "1" else "0")
        )
        cursor.moveToFirst()
        val totalAmount = cursor.getDouble(0)
        cursor.close()
        db.close()
        return totalAmount
    }


    fun update(user: Notes) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(AMOUNT, user.Amount)
        cv.put(DATE, user.Date)
        cv.put(DESCRIPTION, user.Description)
        cv.put(IS_DEBIT, user.Is_Debit)
        val where = "$ID=${user.ID}"
        db.update(TNAME, cv, where, null)
        db.close()
    }

    fun delete(id: Int) {
        val db = writableDatabase
        val where = "$ID=$id"
        db.delete(TNAME, where, null)
        db.close()
    }


    fun getNotes(): List<Notes> {
        val users = ArrayList<Notes>()
        val db = writableDatabase
        val columns = arrayOf(ID, AMOUNT, DATE, DESCRIPTION, IS_DEBIT)
        val cursor:Cursor = db.query(TNAME, columns, null, null, null, null, null)
        while (cursor.moveToNext()) {
                val id = cursor.getInt(0)
                val amount = cursor.getString(1)
                val date = cursor.getString(2)
                val description = cursor.getString(3)
                val is_debit = cursor.getString(4)
                users.add(Notes(id, amount, date,description,is_debit.toBoolean()))
             }
        cursor.close()
        return users
    }





}