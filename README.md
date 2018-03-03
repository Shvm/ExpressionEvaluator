This project is responsible to generate expression from the given json:
Example json: "{ "op": "equal", "lhs": { "op": "add", "lhs": 1, "rhs": { "op": "multiply", "lhs": "x", "rhs": 10 } }, "rhs": 21 }"
 expression of the example json :  1 + ( x * 10 ) = 21
 Rearranges expression in terms of x: x = (21 - 1) / 10;
 Solve for value of x: x = 2.
Prerequisite:
 Maven: Install maven following this link https://maven.apache.org/install.html
Clone this repository.
Run mvn clean install, once build is done run mvn spring-boot:run by default this will start the application on localhost:8080.

"/evaluate" => generates the expression.
"/evaluate-for-x" => rearranges expression in terms of x.
"/solve-for-x" => solves the expression for value of x.



