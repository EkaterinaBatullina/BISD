function updateStatus(taskId, newStatus, projectId) {
    console.log(`Updating task ${taskId} to status ${newStatus}`);
    const statusCell = document.getElementById(`status_${taskId}`);
    if (!statusCell) {
        console.error(`Status cell for task ${taskId} not found!`);
        return;
    }
    statusCell.textContent = newStatus;
    const taskRow = document.getElementById(`task_${taskId}`);
    if (!taskRow) {
        console.error(`Task row for task ${taskId} not found!`);
        return;
    }
    console.log(`Highlighting row: ${taskRow.id}`);
    requestAnimationFrame(() => {
        taskRow.classList.add('highlight');
    });
    setTimeout(() => {
        taskRow.classList.remove('highlight');
        console.log(`Removed highlight from row: ${taskRow.id}`);
    }, 1000);
    fetch("/Sem-01/updateTaskStatus", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `taskId=${taskId}&newStatus=${newStatus}&projectId=${projectId}`
    })
        .then(response => {
            if (response.ok) {
                console.log(`Task ${taskId} status updated to ${newStatus} on server.`);
            } else {
                console.error(`Failed to update task ${taskId} on server.`);
            }
        })
        .catch(error => {
            console.error('Error during status update request:', error);
        });
}

function deleteTask(taskId, projectId) {
    console.log(`Deleting task with ID: ${taskId}, for project with ID: ${projectId}`);
    fetch('/Sem-01/deleteTask', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `taskId=${taskId}&projectId=${projectId}`
    })
        .then(response => {
            if (response.ok) {
                console.log(`Task ${taskId} successfully deleted.`);
                window.location.href = `tasks?projectId=${projectId}`;
            } else {
                console.error(`Error deleting task: ${response.statusText}`);
                alert('Error deleting task');
            }
        })
        .catch(error => {
            console.error('Error during fetch:', error);
            alert('An error occurred while deleting the task.');
        });
}

function deleteProject(projectId) {
    console.log(`Deleting or project with ID: ${projectId}`);
    fetch('/Sem-01/deleteProject', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `projectId=${projectId}`
    })
        .then(response => {
            if (response.ok) {
                console.log(`Project ${projectId} successfully deleted.`);
                window.location.href = 'projects';
            } else {
                console.error(`Error deleting project: ${response.statusText}`);
                alert('Error deleting project');
            }
        })
        .catch(error => {
            console.error('Error during fetch:', error);
            alert('An error occurred while deleting the project.');
        });
}