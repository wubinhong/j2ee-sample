<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set value="${pageContext.request.contextPath }" var="path" scope="application"></c:set>
<div class="navbar navbar-inverse navbar-fixed-top bs-docs-nav" role="banner">
    <div class="container">
        <div class="navbar-header" id="headerId">
            <button class="navbar-toggle" type="button" data-toggle="collapse" data-target=".bs-navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a href="${path }/trade" class="navbar-brand">spring mvc demo</a>
        </div>
        <nav class="collapse navbar-collapse bs-navbar-collapse" role="navigation">
            <ul class="nav navbar-nav">
                <li>
                    <a href="http://3g.weather.com.cn">中国天气通</a>
                </li>
                <li>
                    <a href="http://www1.xn121.com">中国兴农网</a>
                </li>
                <li>
                    <a href="http://www.weather.com.cn/">中国气象频道</a>
                </li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li>
                    <a href="javascript: void(0)">欢迎，${empty sessionScope.userSession.nickName ? sessionScope.userSession.userName : sessionScope.userSession.nickName }</a>
                </li>
            </ul>
        </nav>
    </div>
</div>
<script type="text/javascript">
    $(function () {
    	$('#navId>li').click(function() {
    		$('#navId>li').removeClass('active');
    		$(this).addClass('active');
    	});
    });
</script>