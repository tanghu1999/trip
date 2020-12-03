<%--
  Created by IntelliJ IDEA.
  User: tanghu1999
  Date: 2020/11/10
  Time: 16:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>

    <link type="text/css" rel="stylesheet" href="/layui/css/layui.css" />
    <script type="text/javascript" src="/layui/layui.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/webjars/jquery/3.5.1/jquery.js"></script>
    <script type="text/javascript">
        layui.use(['form','upload','layer','tree'],function () {
            var form = layui.form;
            var upload=layui.upload;
            var layer=layui.layer;
            var tree=layui.tree;


            //初始化树，显示树的数据
            var treeIns = tree.render({
                elem:'#tree1',
                data:getData(),
                click:function(obj){
                    console.log(obj.data.id);
                    $('#categoryId').val(obj.data.id); //为input name为categoryId的标签赋值
                    $('#categoryName').text(obj.data.title); //显示选择的分类编号对应的分类名称
                    layer.closeAll();
                }
            });
            //点击打开按钮
            $('#openBtn').click(function(){
                layer.open({
                    type:1,
                    title:'选择商品分类',
                    area:['300px','400px'],
                    content:$('#tree1')
                });
            });
            //完成图片上传的设置
            //第一张
            upload.render({
                elem:'#upload1',
                url:'/file/uploadOne',
                done:function(result){
                    $('#pic').val(result.path);
                    var img1 = $('<img width="150px" height="100px" />');
                    $(img1).prop('src',result.path);
                    $('#pic1P').append($(img1));
                    layer.msg('上传成功', {icon: 6});
                }
            });
            //第二张
            upload.render({
                elem:'#upload2',
                url:'/file/uploadOne',
                done:function(result){
                    $('#pic2').val(result.path);
                    var img2 = $('<img width="150px" height="100px" />');
                    $(img2).prop('src',result.path);
                    $('#pic2P').append($(img2));
                    layer.msg('上传成功', {icon: 6});
                }
            });

            //完成上传操作，表单提交
            form.on('submit(addContentSubmit)',function(){
                $.ajax({
                    type:'post',
                    url:'/content/insert',
                    data:$('#contentAddForm').serialize(),
                    success:function(result){
                        $('#categoryName').text('');
                        $('#contentAddForm')[0].reset();
                        $('#pic1P').html('');
                        $('#pic2P').html('');
                        layer.msg(result.msg,{icon:6});
                    },
                    dataType:'json'
                });
                return false;
            });





            function getData(){
                resultObj=[];
                $.ajax({
                    type:'post',
                    url:'/contentCat/list',
                    success:function(result){
                        fn(result); //对原始数据处理
                        treeIns.reload({
                            data: resultObj //将处理后的结果赋值的data
                        });
                    },
                    dataType:'json'
                });
            }

            var resultObj=[];
            function fn(jsonData){  //取第一层数据
                for(var i=0;i<jsonData.length;i++) {
                    if (jsonData[i].pid == 0) {
                        resultObj.push(jsonData[i]);
                    }
                }

                getChildren(resultObj,jsonData); //第一个参数为：第一层数据，第二个参数为：所有数据
            }

            function getChildren(nodeList,jsonData){
                nodeList.forEach(function(element,index){
                    //forEach(element,index):第一个参数为：当前元素 第二个参数为：当前元素索引值
                    //console.log(element)

                    //得到子元素
                    element.children=jsonData.filter(function(item,index){
                        /*
                        * filter() 方法创建一个新的数组，新数组中的元素是通过检查指定数组中符合条件的所有元素
                        *array.filter(function(currentValue,index,arr), thisValue)
                        * currentValue：必须。当前元素的值
                        * index：可选。当前元素的索引值
                        *arr：	可选。当前元素属于的数组对象
                        *
                        * thisValue：可选。对象作为该执行回调时使用，传递给函数，用作 "this" 的值。如果省略了 thisValue ，"this" 的值为 "undefined"
                        * */
                        return item.pid===element.id //所有子节点中的pid属性等于第一层数据中的id属性
                    })
                    if(element.children.length > 0){
                        getChildren(element.children,jsonData);
                    }
                })

            }


        });




    </script>


</head>
<body>
    <!--  Add  Content Form begin -->
    <div id="contentFormDiv">
        <div id="tree1" style="display: none"></div>
            <form class="layui-form"  style="margin: 20px 10px"  id="contentAddForm" >
                <div class="layui-form-item">
                    <label class="layui-form-label">分类名称</label>
                    <div class="layui-input-block">
                        <button class="layui-btn" id="openBtn">打开</button>
                        <input type="hidden" class="layui-input" name="categoryId" id="categoryId"/>
                        <span id="categoryName"></span>
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">标题</label>
                    <div class="layui-input-block">
                        <input type="text" name="title" required  lay-verify="required" placeholder="请输入标题" autocomplete="off" class="layui-input">
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">子标题</label>
                    <div class="layui-input-block">
                        <input type="text" name="subTitle" required  lay-verify="required" placeholder="请输入子标题" autocomplete="off" class="layui-input">
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">说明</label>
                    <div class="layui-input-block">
                        <input type="text" name="titleDesc" required  lay-verify="required" placeholder="请输入说明" autocomplete="off" class="layui-input">
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">链接</label>
                    <div class="layui-input-block">
                        <input type="text" name="url" required  lay-verify="required" placeholder="请输入链接" autocomplete="off" class="layui-input">
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">图片1</label>
                    <div class="layui-input-block">
                        <button type="button" class="layui-btn" id="upload1">
                            <i class="layui-icon">&#xe67c;</i>上传图片
                        </button>
                        <input type="hidden" name="pic" id="pic" />
                        <p id="pic1P"></p>
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">图片2</label>
                    <div class="layui-input-block">
                        <button type="button" class="layui-btn" id="upload2">
                            <i class="layui-icon">&#xe67c;</i>上传图片
                        </button>
                        <input type="hidden" name="pic2" id="pic2" />
                        <p id="pic2P"></p>
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">内容说明</label>
                    <div class="layui-input-block">
                        <input type="text" name="content" required  lay-verify="required" placeholder="请输入内容说明" autocomplete="off" class="layui-input">
                    </div>
                </div>
                <div class="layui-form-item">
                    <div class="layui-input-block">
                        <button class="layui-btn" lay-submit lay-filter="addContentSubmit">增加</button>
                        <button type="reset" class="layui-btn layui-btn-primary">重置</button>
                    </div>
                </div>
            </form>
    </div>


</body>
</html>
