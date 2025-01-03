<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update Project</title>
    <link rel="stylesheet" href="${contextPath}/static/css/styles.css">
</head>
<body>
<header>
    <h1>Update Project:
        <#if project?? && project.name??>
            ${project.name}
        <#else>
        <p>Project Not Found</p>
        </#if>
    </h1>
    <nav>
        <ul>
            <li><a href="projects">Back to Projects</a></li>
        </ul>
    </nav>
</header>

<main>
    <#if project??>
    <form id="editProjectForm" method="post" action="/Sem-01/updateProject">
        <input type="hidden" name="projectId" value="${project.id}">

        <label for="projectName">Name:</label>
        <input type="text" id="projectName" name="name" value="${project.name}" required>

        <label for="projectDescription">Description:</label>
        <textarea id="projectDescription" name="description" required>${project.description}</textarea>

        <label for="isPrivate">Private:</label>
        <input type="checkbox" id="isPrivate" name="isPrivate"
                <#if project.checkPrivate?? && project.checkPrivate()>
                    checked
                </#if>
        >

        <button type="submit">Update Project</button>
    </form>
    </#if>
    <#if errorMessage??>
        <div class="error-message">
            <p>${errorMessage}</p>
        </div>
    </#if>
</main>

<footer>
    <p>© 2024 Task Manager</p>
</footer>
</body>
</html>
