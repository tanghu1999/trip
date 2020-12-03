<%--
  Created by IntelliJ IDEA.
  User: tanghu1999
  Date: 2020/11/3
  Time: 16:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>trip后台管理系统</title>
    <link type="text/css" rel="stylesheet" href="/layui/css/layui.css" />
    <script type="text/javascript" src="/layui/layui.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/webjars/jquery/3.5.1/jquery.js"></script>
    <script type="text/javascript">
        //JavaScript代码区域
        layui.use('element', function(){
            var element = layui.element;

            //1.给class=menu增加clickg事件
            $(".menu").click(function () {
                var id = $(this).prop("id");
                var title = $(this).attr("title");
                var url = $(this).attr("url");
                if ($("li[lay-id="+id+"]").length==0) { //判读当前的id在tab中是否已存在,如果不存在就增加一个新的tab,如果存在，就选中
                    element.tabAdd('demo', {
                        title: title
                        , content: '<iframe src='+url+' width="100%" height="100%" frameborder="0"></iframe>'
                        , id: id  // lay-id
                    });
                }
                element.render('tab');
                element.tabChange('demo', id);//选 中状态
            });

        });
    </script>


</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <div class="layui-header">
        <div class="layui-logo">layui 后台布局</div>
        <!-- 头部区域（可配合layui已有的水平导航） -->
        <ul class="layui-nav layui-layout-left">
            <li class="layui-nav-item"><a href="">控制台</a></li>
            <li class="layui-nav-item"><a href="">商品管理</a></li>
            <li class="layui-nav-item"><a href="">用户</a></li>
            <li class="layui-nav-item">
                <a href="javascript:;">其它系统</a>
                <dl class="layui-nav-child">
                    <dd><a href="">邮件管理</a></dd>
                    <dd><a href="">消息管理</a></dd>
                    <dd><a href="">授权管理</a></dd>
                </dl>
            </li>
        </ul>
        <ul class="layui-nav layui-layout-right">
            <li class="layui-nav-item">
                <a href="javascript:;">
                    <img src="http://t.cn/RCzsdCq" class="layui-nav-img">
                    Tom
                </a>
                <dl class="layui-nav-child">
                    <dd><a href="">基本资料</a></dd>
                    <dd><a href="">安全设置</a></dd>
                </dl>
            </li>
            <li class="layui-nav-item"><a href="">退了</a></li>
        </ul>
    </div>

    <div class="layui-side layui-bg-black">
        <div class="layui-side-scroll">
            <!-- 左侧导航区域（可配合layui已有的垂直导航） -->
            <ul class="layui-nav layui-nav-tree"  lay-filter="test">
                <li class="layui-nav-item layui-nav-itemed">
                    <a class="" href="javascript:;">商品管理</a>
                    <dl class="layui-nav-child">
                        <dd><a href="javascript:;" class="menu" id="1" title="商品分类" url="itemCat-list-tree.jsp">商品分类</a></dd>
                        <dd><a href="javascript:;" class="menu" id="2" title="增加商品" url="item-add.jsp">增加商品</a></dd>
                        <dd><a href="javascript:;" class="menu" id="5" title="向索引库中增加数据" url="solr-add.jsp">增加商品到索引库</a></dd>
                    </dl>
                </li>
                <li class="layui-nav-item">
                    <a href="javascript:;">内容管理</a>
                    <dl class="layui-nav-child">
                        <dd><a href="javascript:;" class="menu" id="3" title="内容分类" url="contentCat-list-tree.jsp">内容分类</a></dd>
                        <dd><a href="javascript:;" class="menu" id="4" title="增加内容" url="content-add.jsp">增加内容</a></dd>
                    </dl>
                </li>
                <li class="layui-nav-item"><a href="">云市场</a></li>
                <li class="layui-nav-item"><a href="">发布商品</a></li>
            </ul>
        </div>
    </div>

    <div class="layui-body" >
        <div class="layui-tab layui-tab-card" lay-filter="demo">
            <ul class="layui-tab-title">
                <li class="layui-this">后台管理</li>

            </ul>
            <div class="layui-tab-content">
                <div class="layui-tab-item layui-show">内容1</div>

            </div>
        </div>
    </div>

    <div class="layui-footer">
        <!-- 底部固定区域 -->
        © layui.com - 底部固定区域
    </div>
</div>


</body>
</html>
