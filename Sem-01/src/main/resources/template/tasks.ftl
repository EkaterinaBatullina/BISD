<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Project Tasks</title>
    <link rel="stylesheet" href="${contextPath}/static/css/styles.css"> <!-- подключение CSS -->
</head>
<body>
<header>
    <h1>Tasks for Project: ${projectName}</h1>
    <nav>
        <ul>
            <li><a href="projects">Back to Projects</a></li>
            <li><a href="createTask?projectId=${projectId}">Create New Task</a></li>
        </ul>
    </nav>
</header>

<main>
    <h2>Task List:</h2>
    <#if tasks?? && tasks?size?is_number && (tasks?size > 0)>
        <table>
            <thead>
            <tr>
                <th>Title</th>
                <th>Description</th>
                <th>Status</th>
                <th>Assigned To</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody id="taskList">
            <#list tasks as task>
                <tr id="task_${task.id}">
                    <td>${task.title}</td>
                    <td>${task.description}</td>
                    <td id="status_${task.id}">${task.status}</td>
                    <td>${task.assignedTo.username}</td>
                    <td>
                        <#if task.assignedTo.username == currentUser.username>
                            <button onclick="updateStatus('${task.id?js_string}', 'IN_PROGRESS', '${projectId?js_string}')">Start</button>
                            <button onclick="updateStatus('${task.id?js_string}', 'COMPLETED', '${projectId?js_string}')">Complete</button>
                        </#if>
                        <#if task.project.owner.id == currentUser.id>
                            <button onclick="deleteTask('${task.id?js_string}', '${projectId?js_string}')">Delete Task</button>
                        </#if>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    <#else>
        <p>No tasks found for this project.</p>
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

