<html>
<head>
<script type="text/javascript">
        function displayMessage(){
            alert (document.getElementById('__SP_VIEWSTATE').innerHTML = 'This is from java script.');
            //Android.returnResult();
        }
    </script>
</head>
	<body>
		<form>
			<input type="hidden" name="__SP_VIEWSTATE" value="name=pippo;surname=pluto" />
		</form>
		OK
	</body>
</html>