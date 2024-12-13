<!DOCTYPE html>
<html lang="ru">
<#assign items = data.items>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Список спортивного инвентаря</title>
</head>
<body>
<h1>Список спортивного инвентаря</h1>
<ul>
    <#if items?size > 0>
        <#list items as item>
            <li><a href="/item/${item.id}">${item.name}</a></li>
        </#list>
    <#else>
        <li>Список пуст. Добавьте новый инвентарь.</li>
    </#if>
</ul>
</body>
</html>