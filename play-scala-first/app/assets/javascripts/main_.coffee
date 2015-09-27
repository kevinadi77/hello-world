
# function newdiv(divid) {
#     var node = document.createElement("DIV");
#     var text = document.createTextNode("in div");
#     node.appendChild(text);
#     document.getElementById(divid).appendChild(node);
# }

# @newdiv = (divid) ->
# 	node = document.createElement("DIV")
# 	text = document.createTextNode("in div")
# 	node.appendChild(text)
# 	document.getElementById(divid).appendChild(node)


# function emptydiv(divid) {
#     var divs = document.getElementById(divid);
#     while (divs.firstChild) {
#         divs.removeChild(divs.firstChild);
#     }
# }

# @emptydiv = (divid) ->
# 	divs = document.getElementById(divid)
# 	while divs.firstChild
# 		divs.removeChild(divs.firstChild)


# function newdivrest(divid, msg) {
#     var msgnode = document.getElementById(msg);
#     var divs = document.getElementById(divid);
#     var xhr = new XMLHttpRequest();
#     msgnode.value == "" ? restpath = "/div" : restpath = "/div?msg=" + msgnode.value;
#     xhr.open("GET",restpath,true);
#     xhr.send();
#     xhr.onload = function() { divs.innerHTML += this.responseText };
# }

# @newdivrest = (divid, msg) ->
# 	divs = document.getElementById(divid)
# 	msgnode = document.getElementById(msg)
# 	xhr = new XMLHttpRequest()
# 	restpath = if msgnode.value == "" then "/div" else "/div?msg=" + msgnode.value
# 	xhr.open("GET", restpath, true)
# 	xhr.send()
# 	xhr.onload = () -> divs.innerHTML += this.responseText
