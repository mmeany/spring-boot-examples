
# Objective

I once worked on a project that used [Fitnesse](http://docs.fitnesse.org/FrontPage) to run a series of acceptance tests. `Fitnesse` is basically a Wiki that the QA team can add acceptance criteria to
as `Gherkin` fragments and it is able to execute these acceptance criteria as tests against version of the application. A great idea and still being developed today!

That's the background. With the advent of Testcontainers, I wanted to explore using Cucumber to run a suite of acceptance tests in an environment using Testcontainers to provide realistic
architecture.

Who knows, maybe I will get around to adding a Fitnesse module to this project as well at some point!

For now, we are stuck with tests along the lines of:

```text
Scenario Outline: Mega test all users against all endpoints in a single blast!
	Given user <name>
	When the user calls the <endpoint> endpoint
	Then the call will <result> with status code <status> and <greeting>
	Examples:
	  | name      | endpoint       | result  | status | greeting        |
	  | John      | /greet         | succeed | 200    | Hello John      |
	  | Paula     | /greet         | succeed | 200    | Hello Paula     |
	  | Steve     | /greet         | succeed | 200    | Hello Steve     |
	  | GUEST     | /greet         | succeed | 200    | Hello GUEST     |
	  | ANONYMOUS | /greet         | succeed | 200    | Hello ANONYMOUS |
	  | John      | /member/greet  | succeed | 200    | Hello John      |
	  | Paula     | /member/greet  | succeed | 200    | Hello Paula     |
	  | Steve     | /member/greet  | succeed | 200    | Hello Steve     |
	  | GUEST     | /member/greet  | fail    | 403    |                 |
	  | ANONYMOUS | /member/greet  | fail    | 401    |                 |
	  | John      | /manager/greet | succeed | 200    | Hello John      |
	  | Paula     | /manager/greet | succeed | 200    | Hello Paula     |
	  | Steve     | /manager/greet | fail    | 403    |                 |
	  | GUEST     | /manager/greet | fail    | 403    |                 |
	  | ANONYMOUS | /manager/greet | fail    | 401    |                 |
	  | John      | /admin/greet   | succeed | 200    | Hello John      |
	  | Paula     | /admin/greet   | fail    | 403    |                 |
	  | Steve     | /admin/greet   | fail    | 403    |                 |
	  | GUEST     | /admin/greet   | fail    | 403    |                 |
	  | ANONYMOUS | /admin/greet   | fail    | 401    |                 |
```
