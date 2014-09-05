<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set value="${pageContext.request.contextPath }" var="path" scope="application"></c:set>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<jsp:include page="frame-include.jsp"></jsp:include>
<style type="text/css">
    .error-page {
        text-align: center;
        color: #FFF;
        font-family: Arial;
        text-shadow: 1px 1px 5px rgba(0, 0, 0, 0.6);
    }
    .error-page h1 {
        font-size: 100px;
        color: red;
    }
    .text-danger {
        color: #fa3b3b;
    }
    .text-danger:hover {
        color: red;
    }
</style>
</head>
<body>
<jsp:include page="frame-header.jsp"></jsp:include>
<div class="container" id="contentId">
    <div class="error-page" style="margin-top: 80px;">
        <h1>Error 500</h1>
    </div>
    <div class="row">
        <div class="col-sm-offset-1 col-sm-10">
            <!-- 提示 -->
            <div class="col-sm-offset-1 col-sm-10">
                <div class="panel panel-default" style="margin-top: 30px;">
                    <div class="panel-body">
                        <h1 class="text-center text-danger"><i class="glyphicon glyphicon-warning-sign glyphicon-th-large"></i> 对不起，服务器内部错误！！！</h1>
                        <br>
                        <div class="text-center" style="font-size: 14px; margin-left: 10px; margin-right: 10px;">${info }</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script type="text/javascript">
    $(function () {
        
    });
</script>
</html>