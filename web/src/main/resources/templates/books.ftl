<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Каталог книг — Spring Boot + JPA + Flyway</title>
    <style>
        body { font-family: Arial, sans-serif; background: #f4f6f9; color: #222; margin: 0; padding: 30px; }
        .container { max-width: 960px; margin: 0 auto; background: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.08); }
        .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .btn { display: inline-block; padding: 9px 15px; background: #2563eb; color: #fff; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 14px; }
        .btn-secondary { background: #475569; margin-left: 8px; }
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th, td { padding: 12px; border-bottom: 1px solid #e2e8f0; text-align: left; }
        th { background: #f8fafc; }
        a.link { color: #2563eb; text-decoration: none; font-weight: bold; }
        .users-bar { margin-top: 22px; padding: 14px; background: #f8fafc; border-radius: 6px; border: 1px solid #e2e8f0; }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>Каталог книг (JPA / Hibernate + Flyway)</h1>
        <div>
            <a class="btn" href="/books/add">+ Додати книгу</a>
            <a class="btn btn-secondary" href="/h2-console" target="_blank">H2 Console</a>
        </div>
    </div>

    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Назва книги</th>
            <th>Автор</th>
            <th>Рік</th>
            <th>Дії</th>
        </tr>
        </thead>
        <tbody>
        <#list books as b>
            <tr>
                <td>${b.id}</td>
                <td><a class="link" href="/books/${b.id}">${b.title}</a></td>
                <td>${b.author}</td>
                <td>${b.year?c}</td>
                <td><a class="link" href="/books/${b.id}">Відгуки та деталі &rarr;</a></td>
            </tr>
        </#list>
        </tbody>
    </table>

    <div class="users-bar">
        <strong>Коментарі користувачів:</strong>
        <#list users as u>
            <a class="link" style="margin-left: 14px;" href="/users/${u.id}/comments">@${u.username}</a>
        </#list>
    </div>
</div>
</body>
</html>
