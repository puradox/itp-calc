package com.sambalana.Stack;

import java.util.Scanner;

/* Demo for VectorStack - Infix to Postfix
 *
 * This is a program that converts infix expressions to postfix expressions.
 * The program then outputs the results of the equation.
 *
 * Features include:
 *   - Basic operations
 *   - Negative numbers
 *   - Multiple digit numbers
 *   - Use of nonwhole numbers (fractions, decimals)
 *   - Use of variables (any letter)
 *   - Implied multiplication through the use of parenthesises
 *   - Interactive console input
 *   - Ability to output in scientific notation for large/small numbers
 *   - Correctly handles special cases (dividing by zero)
 *
 * Created by Sam Balana
 */
public class VectorStackDemo {

    /* Used to hold postfix instructions
     * Using this class, we can distinguish between a value and an operator
     */
    private class InputToken {
        private final boolean operator;
        private final double value;

        private InputToken(Double value) {
            this(value, false);
        }

        private InputToken(Character value) {
            this((int) value, true);
        }

        private InputToken(double value, boolean operator) {
            this.value = value;
            this.operator = operator;
        }
    }

    private VectorStack<Integer> digits;        // hold digits, for multiple digits
    private VectorStack<Character> operators;   // holds the operators
    private VectorStack<InputToken> postfix;    // holds the final postfix expression
    private Double[] variables;                 // holds the values of inputted variables
    private Scanner input;                      // used for input
    private int digitCount;                     // the amount of digits that an inputted number contains
    private int decimalPlace;                   // the digit right before the decimal place
    private boolean negative;                   // determines sign of number sequence


    /* Default constructor
     * Initializes all the variables to their defaults
     */
    VectorStackDemo() {
        digits = new VectorStack<Integer>();
        operators = new VectorStack<Character>();
        postfix = new VectorStack<InputToken>();
        variables = new Double[26];
        input = new Scanner(System.in);
        digitCount = 0;
        decimalPlace = -1;
        negative = false;
    }


    /* Main function
     * The entry point of our demo.
     * It asks for input, outputs results, and repeats.
     */
    public static void main(String[] args) {
        VectorStackDemo demo = new VectorStackDemo();
        Scanner input = new Scanner(System.in);
        String expression;
        System.out.print("Type in a mathematical expression. Enter nothing to exit.\n" + " > ");

        while (!(expression = input.nextLine()).equals("")) {
            if (demo.parseExpression(expression))
                System.out.println("Result: " + demo.evaluateExpression());
            else
                System.out.println("Invalid expression");
            System.out.print(" > ");
        }
    }


    /* Combine the all digits of a number into a double
     * This is needed for multiple digit numbers
     */
    private Double convertDigitsToNumber(int digitCount, int decimalPlace) {
        int count = 0;      // keep track of how much digits (for our base of 10)
        double result = 0;  // the result of all digits together

        if (decimalPlace != -1)
            count = decimalPlace - digitCount;

        while (!digits.isEmpty()) {
            // Convert from an ASCII character
            int num = digits.pop() - 48;

            // Multiply the digit by a power of 10 based on it's digit
            result += num * Math.pow(10, count);
            count++;
        }

        // Reset digit variables
        this.digitCount = 0;
        this.decimalPlace = -1;

        // Check for negative flag
        if (negative) {
            this.negative = false;
            return -result;
        }
        else
            return result;
    }


    /* Defines each operator's precedence
     * Needed to take into consideration the order of operations
     */
    private int operatorPrecedence(char c) {
        switch (c) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
            default:
                return 0;
        }
    }


    /* Defines open parenthesis */
    private boolean isOpenParenthesis(char c) { return (c == '{') || (c == '[') || (c == '('); }


    /* Defines close parenthesis */
    private boolean isCloseParenthesis(char c) { return (c == '}') || (c == ']') || (c == ')'); }


    /* Matches open and closed parenthesis
     * Needed to determine invalid parenthesis sets
     */
    private boolean doesMatchParenthesis(int open, char close) {
        return (open == '{' && close == '}') ||
                (open == '[' && close == ']') ||
                (open == '(' && close == ')');
    }


    /* Goes through the process to add an operator
     * This is separate from the rest of the code because it's used in all cases
     * Needed for implicit multiplication
     */
    private void parseOperator(char c) {
        if (!operators.isEmpty()) {

            while (!operators.isEmpty()) {

                if (operatorPrecedence(c) > operatorPrecedence(operators.peek()) || isOpenParenthesis(operators.peek())) {
                    operators.push(c);
                    break;
                } else
                    postfix.push(new InputToken(operators.pop()));
            }

        } else
            operators.push(c);
    }


    /* Parse an infix expression into a postfix expression
     * This is the heart of the program
     */
    public boolean parseExpression(String expression) {
        // Reset stacks
        digits.clear();
        operators.clear();
        postfix.clear();

        // Add parenthesis to whole expression
        expression = "(" + expression + ")";

        // Convert to postfix form
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            // Skip spaces
            if (Character.isWhitespace(c))
                continue;

            // Digit
            if (Character.isDigit(c)) {

                // Check for implicit multiplication - e.g. 2(2) = 2*2
                if (i != 0) {
                    if (Character.isAlphabetic(expression.charAt(i-1)) ||
                        isCloseParenthesis(expression.charAt(i-1)))
                        parseOperator('*');
                }

                digits.push(Integer.valueOf(c));    // add to the number sequence
                digitCount++;
            }
            // Decimal place
            else if (c == '.') {

                // Check for implicit multiplication - e.g. 2(2) = 2*2
                if (i != 0) {
                    if (Character.isAlphabetic(expression.charAt(i-1)) ||
                        isCloseParenthesis(expression.charAt(i-1)))
                        parseOperator('*');
                }

                decimalPlace = digitCount;
            }
            else {
                // Could be the end of a number sequence
                if (!digits.isEmpty()) {
                    postfix.push(new InputToken(convertDigitsToNumber(digitCount, decimalPlace)));      // add the whole number to postfix
                }

                // Variable
                if (Character.isAlphabetic(c)) {

                    // Check for implicit multiplication - e.g. 2(2) = 2*2
                    if (i != 0) {
                        if (Character.isDigit(expression.charAt(i-1)) ||
                            Character.isAlphabetic(expression.charAt(i-1)) ||
                            isCloseParenthesis(expression.charAt(i-1)))
                            parseOperator('*');
                    }

                    c = Character.toUpperCase(c);
                    int index = c - 65;

                    // Check for existing variable
                    if (variables[index] != null)
                        postfix.push(new InputToken(variables[index]));
                    else {
                        // Loop until valid input
                        while (true) {
                            try {
                                System.out.print("Define " + c + ": ");
                                double value = input.nextDouble();
                                postfix.push(new InputToken(value));
                                variables[index] = value;
                                break;
                            } catch (Exception e) {
                                input.next();
                            }
                        }
                    }
                }

                // Open Parenthesis
                else if (isOpenParenthesis(c)) {
                    // Check for implicit multiplication - e.g. 2(2) = 2*2
                    if (i != 0) {
                        if (Character.isDigit(expression.charAt(i-1)) ||
                            Character.isAlphabetic(expression.charAt(i-1)) ||
                            isCloseParenthesis(expression.charAt(i-1)))
                            parseOperator('*');
                    }

                    // Check for negative sign
                    if (i > 1) {
                        if (expression.charAt(i-1) == '-' &&
                            !Character.isDigit(expression.charAt(i-2)) &&
                            !Character.isAlphabetic(expression.charAt(i-2)) &&
                            !isCloseParenthesis(expression.charAt(i-2))) {
                            postfix.push(new InputToken(-1.0));
                            parseOperator('*');
                        }
                    }

                    operators.push(c);
                }

                // Operator
                else if (operatorPrecedence(c) != 0) {

                    // Check for negative sign
                    if (c == '-') {
                        if (!Character.isDigit(expression.charAt(i-1)) &&
                            !Character.isAlphabetic(expression.charAt(i-1)) &&
                            !isCloseParenthesis(expression.charAt(i-1))) {
                            if (!isOpenParenthesis(expression.charAt(i+1)))
                                negative = true;
                            continue;
                        }
                    }

                    parseOperator(c);
                }

                // Close Parenthesis
                else if (isCloseParenthesis(c)) {

                    while (!operators.isEmpty()) {

                        // Check for open parenthesis
                        if (isOpenParenthesis(operators.peek())) {

                            // Check for invalid parenthesis sets
                            if (!doesMatchParenthesis(operators.pop(), c))
                                return false;

                            break;
                        }

                        // Must be an operator
                        postfix.push(new InputToken(operators.pop()));

                    }
                }

                // A non-valid character
                else {
                    return false;
                }

            }
        }

        // Reset variables
        variables = new Double[26];

        return true; // successful
    }


    /* Utility class to reverse a stack */
    private <T> VectorStack<T> reverseVectorStack(VectorStack<T> stack) {
        VectorStack<T> temp = new VectorStack<T>();
        while (!stack.isEmpty())
            temp.push(stack.pop());
        return temp;
    }


    /* Evaluates the postfix expression that was generated from parseExpression()
     * Outputs both the postfix expression and the result of the expression
     */
    public double evaluateExpression() {
        VectorStack<Double> stack = new VectorStack<Double>();            // hold numbers
        VectorStack<InputToken> instructions = reverseVectorStack(postfix); // hold instructions

        System.out.print("Postfix: ");

        // Evaluate the expression
        while (!instructions.isEmpty()) {
            InputToken input = instructions.pop();

            // Numbers
            if (!input.operator) {
                System.out.print(input.value + " ");
                stack.push(input.value);
            }
            // Operators
            else {
                System.out.print((char)input.value + " ");

                double b = stack.pop();
                double a = stack.pop();
                double result;

                // Evaluate each operation
                switch ((char) input.value) {
                    case '+':
                        result = a + b;
                        break;
                    case '-':
                        result = a - b;
                        break;
                    case '*':
                        result = a * b;
                        break;
                    case '/':
                        result = a / b;
                        break;
                    case '^':
                        result = Math.pow(a, b);
                        break;
                    default:
                        return 0;
                }

                // Put result back
                stack.push(result);
            }
        }

        System.out.println();

        // Could be finished
        if (!stack.isEmpty())
            return stack.pop();
        else
            return -666;   // something bad happened
    }

}
