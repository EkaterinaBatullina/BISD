<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Projects</title>
    <link rel="stylesheet" href="${contextPath}/static/css/styles.css">
</head>
<body>
<header>
    <h1>My Projects</h1>
    <nav>
        <ul>
            <li><a href="/Sem-01/createProject">Create New Project</a></li>
            <li><a href="/Sem-01">Go to Home</a></li>
        </ul>
    </nav>
</header>

<main>
    <h2>Projects:</h2>
    <#if projects?? && projects?size?is_number && (projects?size > 0)>
        <table>
            <thead>
            <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Owner</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <#list projects as project>
                <tr>
                    <td>
                        <#if project.owner.id == currentUser.id>
                            <!-- Если это владелец, то делаем название проекта ссылкой на форму редактирования -->
                            <a href="/Sem-01/updateProject?projectId=${project.id}">${project.name}</a>
                        <#else>
                            <!-- Для остальных пользователей просто название проекта -->
                            ${project.name}
                        </#if>
                    </td>
                    <td>${project.description}</td>
                    <td>${project.owner.username}</td>
                    <td>
                        <button onclick="location.href='tasks?projectId=${project.id}'">View Tasks</button>
                        <#if project.owner.id == currentUser.id>
                            <button onclick="deleteProject('${project.id?js_string}')">Delete Project</button>
                        </#if>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    <#else>
        <p>${message!}</p>
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
<script src="${contextPath}/static/js/scripts.js"></script>
</body>
</html>
