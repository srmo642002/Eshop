<%@ page import="eshop.Customer" %><g:if test="${user instanceof Customer}"><g:message code="customer.title.${((Customer)user).sex}"/> ${((Customer)user).lastName}</g:if><g:else>${user.username}</g:else>
رمز شما در فروشگاه اینترنتی زنبیل با موفقیت تغییر پیدا کرد.