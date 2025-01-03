<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create New Task</title>
    <link rel="stylesheet" href="${contextPath}/static/css/styles.css">
</head>
<body>
<header>
    <h1>Create New Task for Project: ${projectName}</h1>
    <nav>
        <ul>
            <li><a href="tasks?projectId=${projectId}">Back to Tasks</a></li>
        </ul>
    </nav>
</header>
<main>
    <form action="createTask" method="post">
        <div>
            <label for="title">Task Title:</label>
            <input type="text" id="title" name="title" required>
        </div>
        <div>
            <label for="description">Description:</label>
            <textarea id="description" name="description" required></textarea>
        </div>
        <div>
            <label for="assignedTo">Assigned To:</label>
            <input type="text" id="assignedTo" name="assignedTo" required>
        </div>
        <div>
            <input type="hidden" name="projectId" value="${projectId}">
        </div>
        <div>
            <button type="submit">Create Task</button>
        </div>
        <#if errorMessage??>
            <div class="error-message">
                <p>${errorMessage}</p>
            </div>
        </#if>
    </form>
</main>
<footer>
    <p>© 2024 Task Manager</p>
</footer>
</body>
</html>
