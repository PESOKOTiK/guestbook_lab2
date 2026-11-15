<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Нова книга в каталозі — ${title}</title>
</head>
<body style="margin:0; padding:0; background-color:#f4f6f9; font-family: Arial, Helvetica, sans-serif; color:#222222;">
    <table width="100%" cellpadding="0" cellspacing="0" style="background-color:#f4f6f9; padding: 30px 15px;">
        <tr>
            <td align="center">
                <table width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:10px; border:1px solid #dce1e7; overflow:hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.05);">
                    <!-- Header + Logo -->
                    <tr>
                        <td style="background-color:#1e293b; padding:24px 30px; text-align:center;">
                            <img src="https://raw.githubusercontent.com/VladPiatachenko/MPF_Labs/lab7-freemaker/web/src/main/resources/static/img/logo.png"
                                 style="width:150px; margin-bottom:12px; display:inline-block;" alt="BookApp">
                            <h1 style="margin:0; font-size:22px; color:#ffffff; letter-spacing:0.5px;">
                                Нова книга у каталозі BookApp
                            </h1>
                        </td>
                    </tr>

                    <!-- Body -->
                    <tr>
                        <td style="padding:28px 30px;">
                            <p style="margin:0 0 16px 0; font-size:15px; line-height:1.5; color:#334155;">
                                Вітаємо! До електронного каталогу бібліотеки щойно додано нове видання:
                            </p>

                            <!-- Стилізована панель повідомлення (фон, рамка, відступи) -->
                            <div style="background-color:#f8fafc; border-left:4px solid #2563eb; border-top:1px solid #e2e8f0; border-right:1px solid #e2e8f0; border-bottom:1px solid #e2e8f0; border-radius:6px; padding:18px 20px; margin:18px 0;">
                                <h2 style="margin:0 0 10px 0; font-size:19px; color:#0f172a;">
                                    ${title}
                                </h2>
                                <p style="margin:6px 0; font-size:14px; color:#334155;">
                                    <strong>Автор:</strong> ${author}
                                </p>
                                <p style="margin:6px 0; font-size:14px; color:#334155;">
                                    <strong>Рік видання:</strong> ${year?c}
                                </p>
                                <p style="margin:6px 0 0 0; font-size:13px; color:#64748b;">
                                    <strong>Дата та час додавання:</strong> ${added}
                                </p>

                                <!-- Умовний блок для раритетних видань (< 2000 року) -->
                                <#if year < 2000>
                                    <div style="margin-top:14px; padding:10px 14px; background-color:#fef3c7; border:1px solid #f59e0b; border-radius:5px;">
                                        <p style="margin:0; color:#a36b00; font-size:14px;">
                                            <b>Раритетне видання!</b> Книга видана до 2000 року (${year?c} р.) та становить особливу бібліографічну цінність.
                                        </p>
                                    </div>
                                </#if>
                            </div>

                            <p style="margin:18px 0 0 0; font-size:13px; color:#64748b; line-height:1.5;">
                                Ви можете переглянути картку книги та залишити відгук у веб-інтерфейсі каталогу за адресою
                                <a href="http://localhost:8080/books" style="color:#2563eb; text-decoration:none;">http://localhost:8080/books</a>.
                            </p>
                        </td>
                    </tr>

                    <!-- Кастомний Footer -->
                    <tr>
                        <td style="background-color:#f1f5f9; border-top:1px solid #e2e8f0; padding:16px 30px; text-align:center;">
                            <p style="margin:0; font-size:12px; color:#64748b;">
                                BookApp &copy; 2025 &mdash; Сумський державний університет (Лабораторна робота №7: FreeMarker + SMTP)
                            </p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>
