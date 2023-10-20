package org.hyperskill.calculator

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.lang.NumberFormatException
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {

    // UI component to display user inputs and results
    val displayEditText: EditText by lazy { findViewById(R.id.displayEditText) }

    // List of button IDs for numbers 0-9
    val buttons = listOf(
        R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
        R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9
    )

    var operate = ""   // Keeps track of the current operation (+, -, *, /, =)
    var active = false // Indicates whether the subtraction sign is active
    var autoCal = false // Indicates whether to automatically continue with the last operation
    var lastValue  = BigDecimal(0) // Stores the last value used for calculation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Prevent soft keyboard from popping up
        displayEditText.inputType = InputType.TYPE_NULL

        // Assign onClick listeners for number buttons
        buttons.forEachIndexed { index, buttonId ->
            findViewById<Button>(buttonId).setOnClickListener {
                addValue(index.toString())
            }
        }

        // Other button listeners
        findViewById<Button>(R.id.dotButton).setOnClickListener { addValue(".") }
        findViewById<Button>(R.id.clearButton).setOnClickListener { clear() }
        findViewById<Button>(R.id.addButton).setOnClickListener { operation("+") }
        findViewById<Button>(R.id.subtractButton).setOnClickListener { operation("-") }
        findViewById<Button>(R.id.divideButton).setOnClickListener { operation("/") }
        findViewById<Button>(R.id.multiplyButton).setOnClickListener { operation("*") }
        findViewById<Button>(R.id.equalButton).setOnClickListener { operation("=") }

    }

    // Handles the arithmetic operations
    fun operation(op : String) {

        //Get values
        var str : String  = displayEditText.getText().toString()
        var c : String  = displayEditText.getHint().toString()

        //this is used to keep last value incase we dont have a second value for calculation
        if(str.length > 0 && str != "-") lastValue = str.toBigDecimal()
        else if ( c.length > 0 && op != "=" ) lastValue = c.toBigDecimal()


        // Perform the specific operation
        when (op) {
            "+" -> handleAddition(str, c)
            "-" -> handleSubstraction(str, c)
            "/" -> handleDivision(str, c)
            "*" -> handleMultiplication(str, c)
            "=" -> handleEquals()
        }
    }

    private fun handleEquals() {
        val b = if (displayEditText.getHint().toString().length > 0) displayEditText.getHint()
            .toString().toBigDecimal() else BigDecimal(0)
        val a = if (displayEditText.getText().toString().length > 0) displayEditText.getText()
            .toString().toBigDecimal() else lastValue

        when (operate) {
            "-" -> {
                val res = b.subtract(a)
                setResult(res)
            }

            "+" -> {
                val res = b.add(a)
                setResult(res)
            }

            "/" -> {
                val res = b.divide(a)
                setResult(res)
            }

            "*" -> {
                val res = b.multiply(a)
                setResult(res)
            }

        }
    }

    private fun handleMultiplication(str: String, c: String) {
        if (str.length == 0 && c == "0") return
        else if (str.length > 0 && c == "0") {
            setValue(str.toBigDecimal(), "*")
        } else if (c != "0") operate = "*"
    }

    private fun handleDivision(str: String, c: String) {
        if (str.length == 0 && c == "0") return
        else if (str.length > 0 && c == "0") {

            setValue(str.toBigDecimal(), "/")
        } else if (c != "0") operate = "/"
    }

    private fun handleSubstraction(str: String, c: String) {
        var str1 = str
        if (str1 == "-") return
        if (active && autoCal) {
            operate = "-"
            active = false
            return
        }
        if (str1.length == 0) {
            str1 = "-"
            displayEditText.setText(str1)
        } else if (str1.length > 0 && c == "0") {
            setValue(str1.toBigDecimal(), "-")
        }
    }

    private fun handleAddition(str: String, c: String) {
        if (str.length == 0 && c == "0") return
        else if (str.length > 0 && c == "0") {

            setValue(str.toBigDecimal(), "+")
        } else if (c != "0") operate = "+"
    }

    private fun setValue(res: BigDecimal, opt: String)
    {
        if (res.scale() <= 0) {
            displayEditText.setHint( res.toBigInteger().toString())
            active = true
        }
        else displayEditText.setHint( res.stripTrailingZeros().toString())

        displayEditText.setText("")

        operate = opt
    }

    private fun setResult(res: BigDecimal) {

        if (res.scale() <= 0) {
            displayEditText.setHint( res.toBigInteger().toString())
            active = true
        }
        else displayEditText.setHint( res.stripTrailingZeros().toString())

        displayEditText.setText("")

        autoCal = true
        //operate = ""
    }

    private fun addValue(value: String) {
        var str : String  = displayEditText.getText().toString()

        try {
            // If the input is a dot and the current string already has a dot, do nothing
            if (value == "." && str.contains("."))  {
                return
            }

            // If the current string is "0" and input is not a dot, reset the string
            if (str == "0" && value != ".") {
                str = ""
            }

            // If the input is a dot and the current string is empty or "0", add "0."
            if (value == "." && (str.isEmpty() || str == "0")) {
                str = "0."
                displayEditText.setText(str)
                return
            }

            if (value == "." && (str == "-")) {
                str = "-0."
                displayEditText.setText(str)
                return
            }


            if (str == "-" && value == "0") return

            // Concatenate the value
            str += value

            // Set the text
            displayEditText.setText(str)


        } catch (e : NumberFormatException) {
            displayEditText.setText(str)
        }
    }

    private fun clear() {
        displayEditText.setText("")
        displayEditText.setHint("0")
        operate = ""
        active = false
        autoCal = false
    }
}