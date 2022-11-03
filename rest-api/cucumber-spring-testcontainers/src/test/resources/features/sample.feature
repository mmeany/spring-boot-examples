Feature: Obtaining a greeting

  # There are a number of users available for testing:
  #  John, a system admin
  #  Paula, a manager
  #  Steve, a member
  #  GUEST, a generic user given a JWT, but not special privileges
  #  ANONYMOUS, a user that does not have a JWT
  #
  # There are greeting endpoints available to each level of user in the system:
  #  /greet         - anyone can call this and obtain a greeting
  #  /member/greet  - only calls with a JWT granting MEMBER role can access this
  #  /manager/greet - only calls with a JWT granting MANAGER role can access this
  #  /admin/greet   - only calls with a JWT granting ADMIN role can access this

  Scenario Outline: Anyone can obtain a greeting from the public endpoint
    Given user <name>
    When the user calls the /greet endpoint
    Then the call will <result> with status code <status> and <greeting>
    Examples:
      | name      | result  | status | greeting        |
      | John      | succeed | 200    | Hello John      |
      | Paula     | succeed | 200    | Hello Paula     |
      | Steve     | succeed | 200    | Hello Steve     |
      | GUEST     | succeed | 200    | Hello GUEST     |
      | ANONYMOUS | succeed | 200    | Hello ANONYMOUS |

  Scenario Outline: Only members can obtain a greeting from the members endpoint
    Given user <name>
    When the user calls the /member/greet endpoint
    Then the call will <result> with status code <status> and <greeting>
    Examples:
      | name      | result  | status | greeting    |
      | John      | succeed | 200    | Hello John  |
      | Paula     | succeed | 200    | Hello Paula |
      | Steve     | succeed | 200    | Hello Steve |
      | GUEST     | fail    | 403    |             |
      | ANONYMOUS | fail    | 401    |             |

  Scenario Outline: Only manager users can obtain a greeting from the manager endpoint
    Given user <name>
    When the user calls the /manager/greet endpoint
    Then the call will <result> with status code <status> and <greeting>
    Examples:
      | name      | result  | status | greeting    |
      | John      | succeed | 200    | Hello John  |
      | Paula     | succeed | 200    | Hello Paula |
      | Steve     | fail    | 403    |             |
      | GUEST     | fail    | 403    |             |
      | ANONYMOUS | fail    | 401    |             |

  Scenario Outline: Only admin users can obtain a greeting from the admin endpoint
    Given user <name>
    When the user calls the /admin/greet endpoint
    Then the call will <result> with status code <status> and <greeting>
    Examples:
      | name      | result  | status | greeting   |
      | John      | succeed | 200    | Hello John |
      | Paula     | fail    | 403    |            |
      | Steve     | fail    | 403    |            |
      | GUEST     | fail    | 403    |            |
      | ANONYMOUS | fail    | 401    |            |

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
