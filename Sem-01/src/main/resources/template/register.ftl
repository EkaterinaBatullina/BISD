<#-- Преобразованная страница регистрации на FreeMarker -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register</title>
    <link rel="stylesheet" href="${contextPath}/static/css/styles.css">
</head>
<body>
<header>
    <h1>Register</h1>
</header>
<main>
    <form action="${contextPath}/register" method="post">
        <div>
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div>
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div>
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" required>
        </div>

        <div>
            <button type="submit">Register</button>
        </div>
        <#if errorMessage??>
            <div class="error-message">
                <p>${errorMessage}</p>
            </div>
        </#if>
    </form>
    <p>Already have an account? <a href="${contextPath}/login">Login</a></p>
</main>
<footer>
    <p>© 2024 Task Manager</p>
</footer>
</body>
</html>
