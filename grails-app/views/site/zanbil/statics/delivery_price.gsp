<%--
  Created by IntelliJ IDEA.
  User: farzin
  Date: 7/22/13
  Time: 6:50 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <g:if test="${session.mobile}">
        <meta name='layout' content='mobile'/>
    </g:if>
    <title>
        <g:message code="footerItems.about"/>
    </title>
</head>

<body>
<div class="page-content">
    <h2>
        هزینه ارسال، مدت زمان ارسال و نحوه پرداخت وجه کالا
    </h2>

    <p>
        روش های ارسال، هزینه، مدت زمان آن و نحوه پرداخت وجه کالا در فروشگاه اینترنتی <g:message code="name"/> بسته به محل سکونت مشتری و نحوه دریافت کالا متفاوت می باشد. ضمناً منظور از پرداخت توسط سیستم POS، امکان پرداخت از طریق کارت های بانکی با دستگاه کارت خوان بی سیم است.
    </p>

    <h3>
        مناطق مختلف شهر تهران
    </h3>
    <ol>
        <li>
            ارسال فوری
            <ul style="margin-right: 10px;list-style-type: disc">
                <li>
                    <b>
                        مدت زمان:
                    </b>
                    حداکثر یک روز کاری
                </li>
                <li>
                    <b>
                        هزینه ارسال:
                    </b>
                    پیک موتوری 4 هزار تومان - ماشین 8 تا 25 هزار تومان - وانت 20 تا 50 هزار تومان
                </li>
                <li>
                    <b>
                        نحوه پرداخت وجه کالا:
                    </b>
                    آنلاین یا درب منزل (POS  یا نقدی)
                </li>
            </ul>
        </li>
        <li>
            ارسال رایگان
            <ul style="margin-right: 10px;list-style-type: disc">
                <li>
                    <b>
                        مدت زمان:
                    </b>
                    حداکثر 4 روز کاری
                </li>
                <li>
                    <b>
                        هزینه ارسال:
                    </b>
                    در صورت قابل حمل بودن توسط پیک موتوری رایگان، درغیر اینصورت برای ارسال رایگان با بخش تامین کالا هماهنگ شود.
                </li>
                <li>
                    <b>
                        نحوه پرداخت وجه کالا:
                    </b>
                    آنلاین یا درب منزل (POS  یا نقدی)
                </li>
            <li>
در ارسال رایگان، مشتری باید خود را برای زمان ارسال با فروشگاه هماهنگ کند.
            </li>
            </ul>
        </li>
    </ol>
    <h3>
        سایر شهرها و یا شهرستانها
    </h3>
    <ol>
        <li>
            ارسال فوری
            <ul style="margin-right: 10px;list-style-type: disc">
                <li>
                    <b>
                        هزینه و مدت زمان:
                    </b>
                    سته به شرکتهای ارائه دهنده خدمات پستی در آن شهر (پست سفارشی، تیپاکس، آرامکس) یا ارسال از طریق باربری، مدت زمان ارسال حدوداً 48 ساعت است. هزینه ارسال نیز بستگی به وزن، ابعاد و ارزش کالا دارد.
                </li>
                <li>
                    <b>
                        نحوه پرداخت وجه کالا:
                    </b>
                آنلاین
                </li>
            </ul>
        </li>
        <li>
            ارسال رایگان
            <br/>
            فقط در موارد خاص و بسته به نوع محصول، تعداد و مبلغ سفارش؛ مشتری باید با بخش تامین کالا هماهنگ کند.
            <ul style="margin-right: 10px;list-style-type: disc">
                <li>
                    <b>
                        مدت زمان:
                    </b>
                    حدوداً 4 روز کاری
                </li>
                <li>
                    <b>
                        نحوه پرداخت وجه کالا:
                    </b>
                آنلاین
                </li>
                <li>
                    در ارسال رایگان، مشتری باید خود را برای زمان ارسال با فروشگاه هماهنگ کند.
                </li>
            </ul>
        </li>
    </ol>
</div>
</body>
</html>