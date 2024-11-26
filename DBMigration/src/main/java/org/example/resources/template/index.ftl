<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/html">
<head>
    <title>Welcome!</title>
    <meta charset="utf-8"/>
</head>
<body>
<h1>${title_page!}</h1>

<h3>Список пользователей</h3>

<div>

    <table>
        <th>ID</th><th>Name</th>
        <!-- Циклическая конструкция -->
        <#list users as user>
            <tr>
                <td>${user.id}</td>
                <td>${user.name}</td>
            </tr>
        </#list>
    </table>

</div>

</body>
</html>