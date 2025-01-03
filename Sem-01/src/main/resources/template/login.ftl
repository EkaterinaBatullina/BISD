<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <link rel="stylesheet" href="${contextPath}/static/css/styles.css">
</head>
<body>
<header>
    <h1>Log in to the system</h1>
</header>
<main>
    <form action="login" method="post">
        <div>
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div>
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div>
            <button type="submit">Login</button>
        </div>
        <#if errorMessage??>
            <div class="error-message">
                <p>${errorMessage}</p>
            </div>
        </#if>
    </form>

    <p>Don't have an account? <a href="register">Register</a></p>
</main>
<footer>
    <p>© 2024 Task Manager</p>
</footer>
</body>
</html>
