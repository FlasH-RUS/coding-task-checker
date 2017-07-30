# coding-task-checker
A simple Spring Roo-based web app to allow doing Russian Code Cup-like challenges.

Just start it using `mvn jetty:run` and share the address with participants - now they can try posting their solutions. The server will validate them and tell you the result. After posting a successful solution to a task the server won't allow you to post more for the same task.

## Features
* Admin access (initially hidden, log in via `/login`): create new tasks, see all solutions
* Public access: see only solutions posted from your IP
