//TAG标签代码
function Show_BlkBlackTab(BlkBlackTabid_num,BlkBlackTabnum){
	for(var i=0;i<4;i++){document.getElementById("BlkBlackTabcontent_"+BlkBlackTabid_num+i).style.display="none";}
	for(var i=0;i<4;i++){document.getElementById("BlkBlackTabmenu_"+BlkBlackTabid_num+i).className="selectd";}
    document.getElementById("BlkBlackTabmenu_"+BlkBlackTabid_num+BlkBlackTabnum).className="BlkBlackTab";
    document.getElementById("BlkBlackTabcontent_"+BlkBlackTabid_num+BlkBlackTabnum).style.display="block";
}
function Show_BlkClassTab(BlkClassTabid_num,BlkClassTabnum){
	for(var i=0;i<7;i++){document.getElementById("BlkClassTabcontent_"+BlkClassTabid_num+i).style.display="none";}
	for(var i=0;i<7;i++){document.getElementById("BlkClassTabmenu_"+BlkClassTabid_num+i).className="class_stay";}
	document.getElementById("BlkClassTabmenu_"+BlkClassTabid_num+BlkClassTabnum).className="class_on";
	document.getElementById("BlkClassTabcontent_"+BlkClassTabid_num+BlkClassTabnum).style.display="block";
}
function Show_BlkNavTab(BlkNavTabid_num,BlkNavTabnum){
	for(var i=0;i<10;i++){document.getElementById("BlkNavTabmenu_"+BlkNavTabid_num+i).className="nav_stay";}
	for(var i=0;i<10;i++){document.getElementById("Tabmenu_"+BlkNavTabid_num+i).style.color="#fff";}
	if(BlkNavTabnum<1){
	document.getElementById("BlkNavTabmenu_"+BlkNavTabid_num+BlkNavTabnum).className="nav_on1";
	}
	else if(BlkNavTabnum>6){
		document.getElementById("BlkNavTabmenu_"+BlkNavTabid_num+BlkNavTabnum).className="nav_on2";
		}else{
	document.getElementById("BlkNavTabmenu_"+BlkNavTabid_num+BlkNavTabnum).className="nav_on";}
	document.getElementById("Tabmenu_"+BlkNavTabid_num+BlkNavTabnum).style.color="#333";
}


function Show_BlkTopTab(BlkTopTabid_num,BlkTopTabnum){
	for(var i=0;i<11;i++){document.getElementById("BlkTopTabcontent_"+BlkTopTabid_num+i).style.display="none";}
	for(var i=0;i<11;i++){document.getElementById("BlkTopTabmenu_"+BlkTopTabid_num+i).className="Top_stay";}
	for(var i=0;i<11;i++){document.getElementById("Topmenu_"+BlkTopTabid_num+i).style.color="#000";}
	document.getElementById("BlkTopTabmenu_"+BlkTopTabid_num+BlkTopTabnum).className="Top_on";
    document.getElementById("BlkTopTabcontent_"+BlkTopTabid_num+BlkTopTabnum).style.display="block";
	document.getElementById("Topmenu_"+BlkTopTabid_num+BlkTopTabnum).style.color="#FF6600";
}

function feedback(id) {
	var f = document.getElementById(id);
	if (f==null) {
		return ;
	}

	var feedback = f.innerHTML.replace(/<.+?>/gim,'');
	if (typeof(feedback) != "undefined" && feedback.trim()!="") {
		alert(feedback);
	}
}

function resetDomain() {
	var ss=document.domain;
	var ii=ss.lastIndexOf('.');
	if(ii>0) {
		if(!isNaN(ss.substr(ii+1)*1))
			return;
		ii=ss.lastIndexOf('.',ii-1);
		if(ii>0)
			document.domain	=ss.substr(ii+1);
	}
}

// 删除左边的空格
String.prototype.ltrim=function() {
	return this.replace(/^\s*/,"");
}
// 删除右边的空格
String.prototype.rtrim=function() {
	return this.replace(/\s*$/,"");
}
// 删除左右两端的空格
String.prototype.trim=function() {
	return this.ltrim().rtrim();
}

function adaptHeight(){
	//div自适应高度
	var scrollHeight= parent.document.body.scrollHeight;
	//var scrollWidth= document.body.scrollWidth;
	var lvlDiv = document.getElementById("leftVerticalLine");
	var rvlDiv = document.getElementById("rightVerticalLine");
	var divHeight=(scrollHeight*1-28)+"px";
	lvlDiv.style.height=divHeight;
	rvlDiv.style.height=divHeight;
	document.getElementById("k_content").style.height=(scrollHeight*1-28)+"px";
	
	$(".panelTable tr:odd").addClass("odd");
}

//resetDomain();