<%--
  Created by IntelliJ IDEA.
  User: tanghu1999
  Date: 2020/11/8
  Time: 13:24
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
        layui.use(['table','tree','layer','form','upload'],function(){
            var tree = layui.tree;
            var table = layui.table;
            var layer = layui.layer;
            var form = layui.form;
            var upload = layui.upload;

            //显示table
            var tabIns = table.render({
                elem:'#myTable', //设置原始table容器的选择器
                id:'myTable',
                url:'/content/list', // 异步数据接口相关参数
                method:'post',
                page:true, // 开启分页
                limit:5, // 每页显示的条数
                limits:[3,4,5], // 每页条数的选择项
                even:true,  // 开启隔行背景
                cols:[[  // 设置表头，值是二位数组
                    {checkbox: true},
                    {field:'id',title:'编号',width:80,sort:true,align:'center'},
                    {field:'categoryId',title:'种类编号',width:100,align:'center'},
                    {field:'title',title:'标题',width:120,align:'center'},
                    {field:'subTitle',title:'小标题',width:100,align:'center'},
                    {field:'titleDesc',title:'标题描述',width:150,align:'center'},
                    {field:'url',title:'路径',width:150,align:'center'},
                    {field:'pic',title:'图片',width:150,align:'center'},
                    {field:'pic2',title:'图片2',width:150,align:'center'},
                    {field:'content',title:'内容',width:150,align:'center'},
                    {field:'created',title:'创建时间',width:130,align:'center'},
                    {field:'updated',title:'更改时间',width:130,align:'center'},
                    {field:'right',title:'操作',width:150,align:'center', toolbar: '#toolbar1'}

                ]]
            });

            //设置tree
            var treeIns = tree.render({
                id:'treeId',
                elem:'#tree1',  //绑定元素
                data:getData(), //数据
                //customOperate:true, //自定义设置树操作
                edit:['add','update','del'], //是否开启节点的操作图标
                //点击tree，table显示相应的数据
                operate:function(obj){
                    console.log(obj);
                    var id = obj.data.id; //id为点击树节点的id属性
                    if(obj.type=='del'){ //如果点击的是删除操作
                        $.ajax({
                            type:'post',
                            url:'/contentCat/delete',
                            data:'id='+id,
                            success:function(result){
                                if(result.status==200){
                                    layer.msg('删除成功',{icon:6});
                                }else{
                                    layer.msg('删除失败',{icon:5});
                                }
                            },
                            dataType:'json',
                            error:function(){
                                layer.msg('删除出错了',{icon:5});
                            }
                        });

                    }else if(obj.type=='update'){ //修改当前树节点
                        $('#editId').val(id);
                        $('#editTitle').val(obj.data.title);
                        layer.open({
                            type:1,
                            title:'编辑内容分类',
                            area:['500px','200px'],
                            content:$('#edit-node')
                        });

                    }else{ //增加一个新节点
                        $('#pid').val(id); //id为点击树节点的id属性
                        layer.open({
                            type:1,
                            title:'新增一个内容分类',
                            area:['500px','200px'],
                            content:$('#add-node')
                        });
                    }
                },
                click:function(obj){
                   // console.log(obj);
                    var id=obj.data.id;  //商品分类的编号
                    tabIns.reload({
                        page:{
                            curr:1
                        },
                        method:'post',
                        where:{
                            categoryId:id
                        }
                    });
                }

            });

            //点击确定增加按钮
            form.on('submit(addOk)',function(obj){
                console.log(obj.field);
                $.ajax({
                    type:'post',
                    url:'/contentCat/insert',
                    data:{
                        title:obj.field.title, //新节点的名称
                        pid:obj.field.id //新节点的父节点
                    },
                    success:function(result) {
                        if (result.status == 200) {
                            layer.msg('添加成功', {icon: 6});
                            //关闭增加的表单
                            layer.closeAll();
                            treeIns.reload({
                                data: getData()
                            });
                        }else{
                            layer.msg('添加失败',{icon:5});
                        }
                    },
                    dataType:'json',
                    error:function(){
                        layer.msg('保存出错了',{icon:5});
                    }
                });
                return false;
            });
            //点击修改确定按钮
            form.on('submit(editOk)',function(obj){
                $.ajax({
                    type:'post',
                    url:'/contentCat/update',
                    data:{
                        id:obj.field.editId,
                        title:obj.field.editTitle
                    },
                    success:function(result){
                        if(result.status==200){
                            layer.msg('修改成功', {icon: 6});
                            //关闭增加的表单
                            layer.closeAll();
                        }else{
                            layer.msg('修改失败', { icon: 5 });
                        }
                    },
                    dataType:'json',
                    error:function() {
                        layer.msg('修改出错了', {icon: 5});
                    }

                });
            });


            //初始化树，显示树的数据
            // var treeIns2 = tree.render({
            //     elem:'#tree2',
            //     data:getData(),
            //     click:function(obj){
            //         console.log(obj.data.id);
            //         $('#categoryId').val(obj.data.id); //为input name为categoryId的标签赋值
            //         $('#categoryName').text(obj.data.title); //显示选择的分类编号对应的分类名称
            //         layer.closeAll();
            //     }
            // });
            //修改、删除实现
            //注：tool 是工具条事件名，test 是 table 原始容器的属性 lay-filter="对应的值"
            table.on('tool(myTableFilter)',function(obj){
                console.log(obj); //当前行
                var id=obj.data.id; //获得当前行数据
                if(obj.event=='del'){ //删除按钮上的lay-event
                    layer.confirm('确认删除吗?',{title:'提示'},function(index){
                        //1.将id传到后台，后台删除这个编号
                        $.post('/content/delete?id='+id,{},function(result){
                            if(result.code==0){
                                tabIns.reload();
                            }
                            layer.msg(result.msg,{icon:6});
                        },'json');
                        //关闭确认框
                        layer.close(index);
                    });
                }else{ //修改

                    $.post('/content/findById',{id:id},function(result){
                        //为表单赋值
                        form.val('updateContentForm',{
                            id:result.id,
                            categoryId:result.categoryId,
                            title:result.title,
                            subTitle:result.subTitle,
                            titleDesc:result.titleDesc,
                            url:result.url,
                            pic:result.pic,
                            pic2:result.pic2,
                            content:result.content,
                        });
                    },'json');
                    //显示要修改的数据
                    layer.open({
                        type:1,
                        title:'修改内容信息',
                        area:['700px','580px'],
                        content:$('#updateContentDiv')
                    });

                    //点击打开按钮
                    $('#openBtn').click(function(){

                        // layer.open({
                        //     type:1,
                        //     title:'选择商品分类',
                        //     area:['300px','400px'],
                        //     content:$('#tree2')
                        // });
                        console.log(111);
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

                }

            });
            //完成数据的修改
            form.on('submit(updateContentSubmit)',function(data){ //响应提交表单button
                $.ajax({
                    type:'post',
                    url:'/content/update',
                    data:$('#updateContentForm').serialize(), //.serialize() 方法创建以标准 URL 编码表示的文本字符串
                    success:function(result){
                        layer.closeAll();
                        tabIns.reload();
                        layer.msg(result.msg,{icon:6});
                    },
                    dataType:'json'
                });
                return false; //阻止表单提交
            });

            //实现批量删除
            $('#delMany').click(function(){
                var ids=[];
                var checkStatus = table.checkStatus('myTable'); //获取表格选中行
                console.log(checkStatus);

                $.each(checkStatus.data,function(index,obj){
                    ids.push(obj.id);
                });
                layer.confirm('确认删除吗?',{title:'提示'},function(index) {
                    $.ajax({
                        type: 'post',
                        url: '/content/deleteMany?ids=' + ids,
                        success: function (result) {
                            tabIns.reload();
                            layer.msg(result.msg);
                        },
                        dataType:'json'
                    });
                    //关闭提示框
                    layer.close(index);
                });

            });

            var resultObj=[];
            function fn(jsonData){  //取第一层数据
                for(var i=0;i<jsonData.length;i++) {
                    if (jsonData[i].pid == 0) {
                        resultObj.push(jsonData[i]);
                    }
                }

                getChildren(resultObj,jsonData); //第一个参数为：第一层数据，第二个参数为：所有数据

            }

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

    <!-- 表格的工具栏-->
    <script type="text/html" id="toolbar1">
        <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
        <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
    </script>

</head>
<body>
    <div class="layui-container">
        <div class="layui-row">
            <div class="layui-col-md3">
                <div id="tree1"></div>
            </div>
            <div class="layui-col-md9">
                <button type="button" id="delMany" class="layui-btn">删除选中</button>
                <table id="myTable" lay-filter="myTableFilter"></table>
            </div>
        </div>
    </div>

    <!-- 对树节点的操作-->
    <div style="width: 600px">
        <!-- 增加一个树节点-->
        <div id="add-node" class="x-body" style="display: none;">
            <form class="layui-form" id="add-form" action="">
                <input type="text" id="pid" name="id" style="display:none;" class="layui-input">
                <div class="layui-form-item center">
                    <label class="layui-form-label" style="width: 100px">节点名字</label>
                    <div class="layui-input-block">
                        <input type="text" name="title" style="width: 240px" class="layui-input">
                    </div>
                </div>
                <div class="layui-form-item">
                    <div class="layui-input-block">
                        <button class="layui-btn" lay-submit lay-filter="addOk">确定</button>
                    </div>
                </div>
            </form>
        </div>
        <!-- 编辑一个树节点-->
        <div id="edit-node" class="x-body" style="display: none;">
            <form class="layui-form" id="edit-form" action="">
                <input type="text" id="editId" name="editId" style="display:none;" class="layui-input">
                <div class="layui-form-item center">
                    <label class="layui-form-label" style="width: 100px">节点名字</label>
                    <div class="layui-input-block">
                        <input type="text" id="editTitle" name="editTitle" style="width: 240px" class="layui-input">
                    </div>
                </div>
                <div class="layui-form-item">
                    <div class="layui-input-block">
                        <button class="layui-btn" lay-submit lay-filter="editOk">确定</button>
                    </div>
                </div>
            </form>
        </div>

    </div>

    <!--  update  ContentForm begin -->
    <div id="updateContentDiv" style="display: none">
        <div id="tree2" style="display: none"></div>
        <form class="layui-form"  style="margin: 20px 10px"  id="updateContentForm" lay-filter="updateContentForm" >
            <div class="layui-form-item">
                <label class="layui-form-label">商品编号</label>
                <div class="layui-input-block">
                    <input type="text" name="id" readonly required  lay-verify="required"  autocomplete="off" class="layui-input">
                </div>
            </div>
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
                    <button class="layui-btn" lay-submit lay-filter="updateContentSubmit">修改</button>
                    <button type="reset" class="layui-btn layui-btn-primary">重置</button>
                </div>
            </div>
        </form>
    </div>

</body>
</html>
