<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>二维码页面</title>
</head>
<body>
		
		订单号:${map.get('out_trade_no')}<br>
		应付金额:${map.get('total_fee')/100 }元<br>
		<img id="qrious">
</body>
<script type="text/javascript" src="/js/qrious.min.js" ></script>
<script type="text/javascript" src="/js/jquery-3.1.1.min.js"></script>
<script type="text/javascript">
		 var qr = new QRious({
			 element:document.getElementById('qrious'),
			 size:250,
			 value:'${map.get("code_url")}',
			 level:'L'
		 });
		 /* 展示二维码的时候就要查看订单状态 */
		 queryPayStatus('${map.get("out_trade_no")}');
		 //查询订单
		 function queryPayStatus(out_trade_no){
			 $.ajax({
				 type:'post',
				 url:'/payStatus', //显示二维码后发送请求查看订单状态
				 data:{"out_trade_no":out_trade_no},
				 dataType:'json',
				 success:function(data){
				 	console.log(data);
					 if(data.wxResult.success==true){
						 window.location.href="/paySuccess.jsp";
					 }else{
						 window.location.href="/payFail.jsp";
					 }
				 }
			 })
		 }
		</script>
</html>