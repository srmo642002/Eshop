<%@ page contentType="text/html;charset=UTF-8" %>
${customer.username}
<br/>
با سلام
<br/>
<br/>
به منظور فعال سازی حساب کاربری خود بر روی لینک زیر کلیک کنید:
<br/>
<a href="http://www.zanbil.ir${createLink(controller: 'customer', action: 'activate', params: [id:customer.id, code: "${new Date().timeString}_activate_${customer.id}".encodeAsBase64()])}">
    فعال سازی حساب کاربری
</a>