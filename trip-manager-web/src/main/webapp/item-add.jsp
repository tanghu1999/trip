<%--
  Created by IntelliJ IDEA.
  User: tanghu1999
  Date: 2020/11/11
  Time: 19:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>

    <style type="text/css">
        .layui-form-label {
            width:150px !important;
        }
        .layui-input {
            width:90% !important;
        }
        #div_prev {
            width:700px !important;
            margin-left:180px !important;
        }
        .image-container {
            display: inline-block;
        }


    </style>

    <link rel="stylesheet" href="/kindeditor-4.1.7/themes/default/default.css" />
    <link rel="stylesheet" href="/kindeditor-4.1.7/plugins/code/prettify.css" />
    <script charset="utf-8" src="/kindeditor-4.1.7/kindeditor.js"></script>
    <script charset="utf-8" src="/kindeditor-4.1.7/lang/zh_CN.js"></script>
    <script charset="utf-8" src="/kindeditor-4.1.7/plugins/code/prettify.js"></script>

    <link type="text/css" rel="stylesheet" href="/layui/css/layui.css" />
    <script type="text/javascript" src="/layui/layui.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/webjars/jquery/3.5.1/jquery.js"></script>
    <script type="text/javascript">
        layui.use(['form','upload','layer','tree'],function () {
            var form = layui.form;
            var upload = layui.upload;
            var layer = layui.layer;
            var tree = layui.tree;


            //点击打开按钮显示树节点
            $('#openBtn').click(function(){
                layer.open({
                    type:1,
                    title:'选择商品分类',
                    area:['300px','400px'],
                    content:$('#tree1')
                });
            });

            //显示树节点
            var treeIns = tree.render({
                elem:'#tree1',
                data:getData(),
                click:function(obj){
                    var id=obj.data.id;
                    var title = obj.data.title;
                    $('#cid').val(id);
                    $('#cname').text(title);
                    layer.closeAll();
                }
            });

            //完成多个图片上传
            var url = '/file/uploadMultiple';
            var resultData = new Array();
            var uploadInst = upload.render({
                elem: '#drapUpload',
                url: url,
                accept: 'file',
                size: 1000 * 1024, //限制文件大小，单位 KB
                multiple: true, //允许多文件上传
                auto: false, //选完文件后不要自动上传
                bindAction: '#doUpload', //指定一个按钮触发上传
                choose: function (obj) {
                    var files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
                    //图像预览，如果是多文件，会逐个添加。(不支持ie8/9)
                    obj.preview(function(index, file, result){
                        if (files.size > 0 && $('#div_prev').find('img').length === 0) {
                            $('#div_prev').empty();
                        }
                        // 添加图片 ImgPreview-预览的dom元素的id
                        if($('#div_prev').children().length<4){
                            $('#div_prev').append('<div class="image-container"  id="container'+index+'">'
                                + '<div style="dispaly:inline-block" class="delete-css">'
                                + '<button id="upload_img_'+index+'" class="layui-btn layui-btn-danger layui-btn-xs">删除</button></div>'
                                + '<img id="showImg'+index+'" style="width: 120px;height: 68px; margin:0px;cursor:pointer;"src="' + result + '" alt="' + file.name + '"></div>');
                        }

                        //删除某图片
                        $("#upload_img_" + index).bind('click', function () {
                            delete files[index];
                            $("#container"+index).remove();
                        });

                        //某图片放大预览
                        $("#showImg"+index).bind('click',function () {
                            var width = $("#showImg"+index).width();
                            var height = $("#showImg"+index).height();
                            var scaleWH = width/height;
                            var bigH = 600;
                            var bigW = scaleWH*bigH;
                            if(bigW>900){
                                bigW = 900;
                                bigH = bigW/scaleWH;
                            }

                            // 放大预览图片
                            layer.open({
                                type: 1,
                                title: false,
                                closeBtn: 1,
                                shadeClose: true,
                                area: [600 + 'px', 300 + 'px'], //宽高
                                content: "<img width='"+bigW+"' height='"+bigH+"' src=" + result + " />"
                            });
                        });
                    });

                },
                before: function (obj) { //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
                },
                done: function (res, index, upload) {
                    layer.closeAll('loading');
                    var data = res.data;
                    if (res.code == 0) {
                        for (var i = 0; i < data.length; i++) {
                            resultData[resultData.length + i] = data[i];
                            console.info(data[i])
                        }
                    }

                },
                allDone: function(obj){ //当文件全部被提交后，才触发
                    layer.msg('上传成功');
                    $('#div_prev').html('');
                    $("#image").val(resultData.join(","));
                    console.info(resultData.join(","))

                }
                ,error: function () {
                    layer.msg('上传失败');
                    layer.closeAll('loading');
                }
            });

            //提交表单
            form.on('submit(addItemSubmit)',function (obj) {
                $.ajax({
                    type:'post',
                    url:'/item/insert',
                    data:obj.field,
                    success:function (result) {
                        //清空表单中的数据
                        $('#cname').text('');
                        $("#itemAddForm")[0].reset();
                        layer.msg(result.msg)
                    },
                    dataType:"json"
                });
                return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
            })


            function getData(){
                resultObj=[];
                //取得树中的数据
                $.ajax({
                    type:'post',
                    url:'/itemCat/list',
                    success:function(result){
                        fn(result); //对原始数据处理
                        treeIns.reload({
                            data:resultObj //将处理后的结果赋值给data
                        })
                    }
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

    <script type="text/javascript">
          // KindEditor.ready(function(K) {
          //     var editor1 = K.create('textarea[name="content1"]', {
          //         cssPath : '/kindeditor-4.1.7/plugins/code/prettify.css',
          //         uploadJson : '/kindeditor-4.1.7/jsp/upload_json.jsp',
          //         fileManagerJson : '/kindeditor-4.1.7/jsp/file_manager_json.jsp',
          //         allowFileManager : true,
          //         afterCreate : function() {
          //             var self = this;
          //             K.ctrl(document, 13, function() {
          //                 self.sync();
          //                 document.forms['example'].submit();
          //             });
          //             K.ctrl(self.edit.doc, 13, function() {
          //                 self.sync();
          //                 document.forms['example'].submit();
          //             });
          //         }
          //     });
          //     prettyPrint();
          // });
    </script>



</head>
<body>

<!--  Add  ItemForm begin -->
<div id="itemFormDiv">
    <div id="tree1" style="display: none"></div>
    <form class="layui-form"  style="margin: 20px 10px"  id="itemAddForm" >
        <div class="layui-form-item">
            <label class="layui-form-label">商品类型名称</label>
            <div class="layui-input-block">
                <button class="layui-btn" id="openBtn">打开</button>
                <input type="hidden" class="layui-input" name="cid" id="cid"/>
                <span id="cname"></span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">标题</label>
            <div class="layui-input-block">
                <input type="text" name="title" required  lay-verify="required" placeholder="请输入标题" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">卖点</label>
            <div class="layui-input-block">
                <input type="text" name="sellPoint" required  lay-verify="required" placeholder="请输入卖点" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">价格</label>
            <div class="layui-input-block">
                <input type="text" name="price" required  lay-verify="required" placeholder="请输入价格" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">库存数量</label>
            <div class="layui-input-block">
                <input type="text" name="num" required  lay-verify="required" placeholder="请输入库存数量" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">条形码</label>
            <div class="layui-input-block">
                <input type="text" name="barcode" required  lay-verify="required" placeholder="请输入条形码" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">商品图片[最多选4张]</label>
            <div class="layui-input-inline">
                <button type="button" class="layui-btn" id="drapUpload">
                    <i class="layui-icon">&#xe67c;</i>上传图片
                </button>
                <button type="button" class="layui-btn" id="doUpload">上传</button>
            </div>
            <div id="div_prev" class="layui-input-inline"></div>
            <input type="hidden" name="image" id="image"/>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">商品描述</label>
            <div class="layui-input-block">
                <textarea name="content1" cols="100" rows="8" style="width:700px;height:200px"></textarea>
            </div>
        </div>

        <div class="layui-form-item">
            <div class="layui-input-block">
                <button class="layui-btn" lay-submit lay-filter="addItemSubmit">增加</button>
                <button type="reset" class="layui-btn layui-btn-primary">重置</button>
            </div>
        </div>
    </form>
</div>






</body>
</html>
