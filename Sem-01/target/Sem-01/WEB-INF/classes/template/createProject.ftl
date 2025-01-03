<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create New Project</title>
    <link rel="stylesheet" href="${contextPath}/static/css/styles.css">
</head>
<body>
<header>
    <h1>Create New Project</h1>
    <nav>
        <ul>
            <li><a href="projects">Back to Projects</a></li>
        </ul>
    </nav>
</header>
<main>
    <form action="createProject" method="post">
        <div>
            <label for="name">Project Name:</label>
            <input type="text" id="name" name="name" required>
        </div>
        <div>
            <label for="description">Description:</label>
            <textarea id="description" name="description" required></textarea>
        </div>
        <div>
            <label for="isPrivate">Private Project:</label>
            <input type="checkbox" id="isPrivate" name="isPrivate">
        </div>
        <div>
            <button type="submit">Create Project</button>
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
