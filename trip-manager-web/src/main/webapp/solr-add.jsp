<%--
  Created by IntelliJ IDEA.
  User: tanghu1999
  Date: 2020/11/16
  Time: 14:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>将商品表中的数据导入到solr索引库中</title>
    <link type="text/css" rel="stylesheet" href="/layui/css/layui.css" />
    <script type="text/javascript" src="/layui/layui.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/webjars/jquery/3.5.1/jquery.js"></script>
    <script type="text/javascript">
        layui.use(['layer'],function() {
            var layer = layui.layer;

            $('#importBtn').click(function () {
                $.ajax({
                    type: 'post',
                    url: '/item/addSolr',
                    success: function (result) {
                        layer.msg(result.msg);
                    },
                    dataType: 'json'
                });
            });



        });








    </script>
</head>
<body>
    <button id="importBtn" class="layui-btn">将数据导入到solr索引库</button>
</body>
</html>
