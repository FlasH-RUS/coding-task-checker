# coding-task-checker
A simple Spring Roo-based web app to allow doing Russian Code Cup-like challenges.

## Setup
1. Search for/come up with task(s) you want to give to your participants (e.g. http://www.russiancodecup.ru/en/tasks/, etc.)
2. Create validation services for the tasks you've chosen (there's already one inplace - see `MartianVolleyballValidationService`)
3. Start the server (e.g. with `mvn jetty:run`), login as admin (via `/login`) and create your tasks specifying time limit in seconds for execution + full class name of corresponding validation service.
4. Share the address with participants - now they can try posting their solutions.

After posting a solution server will try to compile it and run tests against the compiled solution (using the validation service specified in the task). In case your solution have passed all the test you won't be allowed to post more for the same task.

## Grants
* Admin access (initially hidden, log in via `/login`): create new tasks, see all solutions
* Public access: see only solutions posted from your IP
