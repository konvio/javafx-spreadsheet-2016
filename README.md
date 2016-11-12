#Spreadsheet

##Overview
A spreadsheet is an interactive table for storing, analyzing and processing data. A cell can contain
numbers, operators, braces and references to other cells. A spreadsheet dynamically evaluates value in each cell.

##Main window
![main_window](http://i.imgur.com/lF9ZHxH.png)
The main window contains editable grid, text field to display entered formula in focused cell and
button to toggle Formulas mode.

##Supported operations
* Addition '+'

* Subtraction '-'

* Multiplication '*'

* Division '/'

* Power '^'

* Bitwise AND '&' and OR '|'

##Reference format
A reference is a latin letter, followed by number from 1 to 99.

##Long arithmetic
Long arithmetic is used to avoid integer overflow.
![long_arithmetic](http://i.imgur.com/LNBFa7m.png)

##Operators precedence
Operators precedence are respected. 
![operator_precedence](http://i.imgur.com/Q8kSY3I.png)

##Formulas Mode
When Formulas Mode is toggled, cells display originally typed formula instead of evaluated value.
![formula_mode](http://i.imgur.com/XqZs7HR.png)

## Text parsing
A Lexer is implemented in order to parse text. The Lexer uses regular expressions
and named capturing groups to extract information from entered text.

##Algorithms
* **Tarjan's algorithm** to detect cycled references
* **Topological sorting** to determine in what order evaluate cells
* **Shunting-yard algorithm** to evaluate expression and respect precedence and braces