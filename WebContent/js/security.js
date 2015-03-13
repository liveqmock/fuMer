function SecuritySearch(url, formName, inputName, tableClass, hintId, size, maxSize, disableEnter) {
    this.baseUrl = url;
    this.formName = formName;
    this.inputName = inputName;
    this.tableClass = tableClass;
    this.hintId = hintId;
    this.maxSize = maxSize == -1 ? 100 : maxSize;
    this.disableEnter = disableEnter;
    this.tempName = "temp" + this.inputName;
    this.layerObject = getElement(this.formName, this.inputName + "div").parentNode;
    this.tempObject = getElement(this.formName, this.tempName);
    this.targetObject = getElement(this.formName, this.inputName);
    this.request = null;
    this.displaySize = size;
    this.rowHeight = 23;
    this.inited = false;
    this.waitingMillSec = 50;
    this.keyUpTime = null;
}

SecuritySearch.prototype.getParams = function() {
    //        return "&maxCount=" + this.maxSize + "&key=" + encodeURI(this.tempObject.value);
    return "&maxCount=" + this.maxSize + "&key=" + encode64(escape(this.tempObject.value));
}

SecuritySearch.prototype.search = function() {
    if (!this.inited) {
        this.addClickEvent();
        this.inited = true;
    }
    this.clearHint();
    this.targetObject.value = "";
    if (!this.tempObject.value.isEmpty()) {
        this.connect();
    }
}

SecuritySearch.prototype.connect = function() {
    this.keyUpTime = (new Date()).getTime();
    setTimeout("this.doSearch()", this.waitingMillSec);
}

SecuritySearch.prototype.doSearch = function() {
    var nowTime = (new Date()).getTime();
    if (nowTime - this.keyUpTime < this.waitingMillSec) return;
    this.doBeforeCall();
    this.request = this.getRequest();
    if (this.request) {
        var data = this.getParams();
        this.request.open('POST', this.baseUrl, true);
        this.request.setRequestHeader("content-length", data.length);
        this.request.setRequestHeader("content-type", "application/x-www-form-urlencoded");
        this.request.onreadystatechange = this.getResult;
        this.request.send(data);
    }
}

SecuritySearch.prototype.doBeforeCall = function() {

}

SecuritySearch.prototype.doAfterCall = function() {

}

SecuritySearch.prototype.displayLayer = function(logic) {
    var _style = this.layerObject.style;
    var site = this.tempObject;
    if (logic) {
        var setTop = this.tempObject.offsetTop;
        var setLeft = this.tempObject.offsetLeft;
        while (site = site.offsetParent) {
            setTop += site.offsetTop;
            setLeft += site.offsetLeft;
        }
        _style.left = setLeft;
        _style.top = setTop + 18;
        _style.display = "";
        _style.visibility = "visible";
    } else {
        _style.display = "none";
        _style.visibility = "hidden";
    }
}

SecuritySearch.prototype.keyUp = function(event) {
    if (event.keyCode == 40 || event.keyCode == 38) {

    }
    else if (event.keyCode == 27) {   //Esc
        this.lastTR = null;
        this.displayLayer(false);
    }
    else if (event.keyCode == 13) {    //enter
        if (this.layerObject.style.display == 'none' || this.layerObject.style.visibility == 'hidden') {
            return;
        }
        var tables = this.layerObject.getElementsByTagName("TABLE");
        var tableRows = null;
        if (tables.length > 0) tableRows = tables[0].rows;
        if (tableRows.length == 1) this.lastTR = tableRows[0];
        if (this.lastTR != null) {
            this.tempObject.value = this.lastTR.getAttribute("id");
            this.targetObject.value = this.lastTR.getAttribute("id");
            this.displayLayer(false);
            this.doAfterCall();
        }
    }
    else {  //search
        this.lastTR = null;
        this.displayLayer(false);
        this.search();
    }
}

SecuritySearch.prototype.keyDown = function(event) {
    if (event.keyCode == 40) { //keydown
        if (this.lastTR != null) {
            this.lastTR.className = this.lastTR.className.replace(/\bsearch_over\b/g, '');
            var _parentNode = this.lastTR.parentNode;
            this.lastTR = this.lastTR.rowIndex == (_parentNode.rows.length - 1) ? _parentNode.rows[0] : _parentNode.rows[this.lastTR.rowIndex + 1];
            this.lastTR.className += ' search_over';
            if (this.lastTR.rowIndex >= this.displaySize) {
                this.layerObject.scrollTop += this.rowHeight;
            }
            if (this.lastTR.rowIndex == 0) {
                this.layerObject.scrollTop = 0;
            }
        } else {
            if (this.layerObject.style.display == 'none' || this.layerObject.style.visibility == 'hidden') {
                return;
            }
            var tables = this.layerObject.getElementsByTagName("TABLE");
            var tableRows = null;
            if (tables.length > 0) tableRows = tables[0].rows;
            if (tableRows.length > 0) this.lastTR = tableRows[0];
            if (this.lastTR != null) {
                if (this.lastTR.rowIndex >= this.displaySize) {
                    this.layerObject.scrollTop += this.rowHeight;
                }
                if (this.lastTR.rowIndex == 0) {
                    this.layerObject.scrollTop = 0;
                }
                var _clicked = this.lastTR.className.match(/\bsearch_over\b/);
                if (_clicked == null) {
                    this.lastTR.className += ' search_over';
                }
            }
        }
    }
    else if (event.keyCode == 38) {   //keyup
        if (this.lastTR != null) {
            this.lastTR.className = this.lastTR.className.replace(/\bsearch_over\b/g, '');
            _parentNode = this.lastTR.parentNode;
            this.lastTR = this.lastTR.rowIndex == 0 ? _parentNode.rows[_parentNode.rows.length - 1] : _parentNode.rows[this.lastTR.rowIndex - 1];
            this.lastTR.className += ' search_over';
            if (this.lastTR.rowIndex >= this.displaySize - 1) {
                this.layerObject.scrollTop -= this.rowHeight;
            }
            if (this.lastTR.rowIndex == _parentNode.rows.length - 1) {
                this.layerObject.scrollTop = _parentNode.rows.length * this.rowHeight;
            }
        } else {
            if (this.layerObject.style.display == 'none' || this.layerObject.style.visibility == 'hidden') {
                return;
            }
            tables = this.layerObject.getElementsByTagName("TABLE");
            tableRows = null;
            if (tables.length > 0) tableRows = tables[0].rows;
            if (tableRows.length > 0) this.lastTR = tableRows[tableRows.length - 1];
            if (this.lastTR != null) {
                if (this.lastTR.rowIndex >= this.displaySize - 1) {
                    this.layerObject.scrollTop -= this.rowHeight;
                }
                if (this.lastTR.rowIndex == tableRows.length - 1) {
                    this.layerObject.scrollTop = tableRows.length * this.rowHeight;
                }
                _clicked = this.lastTR.className.match(/\bsearch_over\b/);
                if (_clicked == null) {
                    this.lastTR.className += ' search_over';
                }
            }
        }
    }
    else if (this.disableEnter && event.keyCode == 13) {
        event.cancelBubble = true;
        event.returnValue = false;
    }
}

SecuritySearch.prototype.displayResult = function(xmlDom) {
    this.layerObject.innerHTML = "";
    if (xmlDom == null) {
        //            this.giveHint("result not found!");
        return;
    }
    var dataRoot = this.getNode(xmlDom, "dataRoot");
    var dataItem = this.getNode(dataRoot[0], "dataItem");
    if (dataItem.length == 0) {
        //            this.giveHint("result not found!");
        return;
    }
    var resultStr = "<input type='hidden' name='" + this.inputName + "div' />";
    resultStr += "<iframe style=\"position: absolute;width:100%; z-index: -1; filter: alpha(opacity=0);height:" + (this.rowHeight * dataItem.length + 2) + "px;\"></iframe>";
    resultStr += "<table broder='0' cellspacing='0' cellpadding='0' class='" + this.tableClass + "'>";
    for (var i = 0; i < dataItem.length; i++) {
        var id = this.getNodeValue(this.getNode(dataItem[i], "id"), 0);
        var ticker = this.getNodeValue(this.getNode(dataItem[i], "ticker"), 0);
        var name = this.getNodeValue(this.getNode(dataItem[i], "name"), 0);
        resultStr += "<tr id='" + id + "' class='" + (i % 2 == 0 ? "search_odd" : "search_even")
                + "' onmouseover='SecuritySearch.mouseover(this);' onmouseout='javascript:SecuritySearch.mouseout(this);'" +
                     " onclick='javascript:SecuritySearch.click(this,\"" + this.formName + "\",\"" + this.tempName + "\",\"" + this.inputName + "\");' >";
        resultStr += "<td><strong>" + (i + 1) + "</strong></td><td>" + ticker + "</td><td>" + name + "</td></tr>";
    }
    resultStr += "</table>";
    if (dataItem.length > this.displaySize) {
        this.layerObject.style.height = (this.rowHeight * this.displaySize ) + "px";
    } else {
        this.layerObject.style.height = (this.rowHeight * dataItem.length + 2) + "px";
    }
    this.layerObject.innerHTML = resultStr;
    this.displayLayer(true);
}

SecuritySearch.prototype.getResult = function() {
    if (this.request.readyState == 4) {
        if (this.request.status == 200) {
            this.displayResult(this.request.responseXML);
            this.request.abort();
            this.request = null;
        }
    }
}

SecuritySearch.prototype.getNode = function(obj, tagName) {
    return obj.getElementsByTagName(tagName);
}

SecuritySearch.prototype.getNodeValue = function(node, site) {
    var nodeValue = node.item(site).firstChild.nodeValue;
    return decodeURIComponent((nodeValue));
}

SecuritySearch.prototype.encoder = function(str) {
    return encodeURI(str);
}

SecuritySearch.prototype.getRequest = function() {
    try {
        if (window.XMLHttpRequest) {
            return new XMLHttpRequest();
        } else if (window.ActiveXObject) {
            return new ActiveXObject("Microsoft.XMLHTTP");
        } else {
            this.giveHint("您使用的浏览支持XMLHttpRequest，请使用IE6.0！");
        }
    } catch(e) {
        this.giveHint(e);
        this.giveHint("您可能限制浏览器访问某些安全控件，\n请点击浏览器上方的[工具]-->[INTERNET选项]-->[安全]-->选择[默认级别]，点击[确定]");
    }
    return null;
}

SecuritySearch.prototype.giveHint = function(s) {
    var obj = this.hintId == null ? null : document.getElementById(this.hintId);
    if (obj == null) {
        alert(s);
    } else {
        obj.innerHTML = s;
    }
}

SecuritySearch.prototype.clearHint = function() {
    var obj = this.hintId == null ? null : document.getElementById(this.hintId);
    if (obj != null) {
        obj.innerHTML = "";
    }
}

SecuritySearch.prototype.addClickEvent = function() {
    var oldonclick = document.body.onclick;
    var tempObj = this.tempObject;
    var layerObj = this.layerObject;
    var callBackFunc = this.doAfterCall;
    if (typeof oldonclick != 'function') {
        document.body.onclick = function(e) {
            var obj = document.all ? (event ? event.srcElement : null) : (e.target);
            if (layerObj.contains(obj)) {
                callBackFunc();
            } else if (obj != tempObj) {
                hiddenLayer(layerObj);
            }
        }
    }
    else {
        document.body.onclick = function(e) {
            oldonclick(e);
            var obj2 = document.all ? (event ? event.srcElement : null) : (e.target);
            if (layerObj.contains(obj2)) {
                callBackFunc();
            } else if (obj2 != tempObj) {
                hiddenLayer(layerObj);
            }
        }
    }
}

SecuritySearch.click = function(tr, fm, tempName, targetName) {
    hiddenLayer(getElement(fm, targetName + "div").parentNode);
    getElement(fm, tempName).value = tr.getAttribute("id");
    getElement(fm, targetName).value = tr.getAttribute("id");
}

SecuritySearch.mouseover = function(tr) {
    var _clicked = tr.className.match(/\bsearch_over\b/);
    if (_clicked == null) {
        tr.className += ' search_over';
    }
}

SecuritySearch.mouseout = function(tr) {
    tr.className = tr.className.replace(/\bsearch_over\b/g, '');
}

function SecurityModleBean(id, ticker, name, cnSpell, cnShortPsell) {
    this.id = id;
    this.ticker = ticker;
    this.name = name;
    this.cnSpell = cnSpell;
    this.cnShortSpell = cnShortPsell;
}

function SecurityClientSearch(sourceArray, formName, inputName, tableClass, hintId, size, maxSize, disableEnter) {
    this.sourceArray = sourceArray;
    this.formName = formName;
    this.inputName = inputName;
    this.tableClass = tableClass;
    this.hintId = hintId;
    this.maxSize = maxSize == -1 ? 100 : maxSize;
    this.disableEnter = disableEnter;
    this.tempName = "temp" + this.inputName;
    this.layerObject = getElement(this.formName, this.inputName + "div").parentNode;
    this.tempObject = getElement(this.formName, this.tempName);
    this.targetObject = getElement(this.formName, this.inputName);
    this.displaySize = size;
    this.rowHeight = 23;
    this.inited = false;
}

SecurityClientSearch.prototype.search = function() {
    if (!this.inited) {
        this.addClickEvent();
        this.inited = true;
    }
    this.clearHint();
    this.targetObject.value = "";
    if (this.tempObject.value.isEmpty()) {
        return;
    }
    var key = this.tempObject.value.trim();
    this.doBeforeCall();
    var result = new Array();
    var len = 0;
    for (var i = 0; i < this.sourceArray.length; i++) {
        if (this.isValid(key, this.sourceArray[i])) {
            result[len] = this.sourceArray[i];
            len++;
        }
    }
    this.displayResult(result);
}

SecurityClientSearch.prototype.isValid = function(key, bean) {
    return bean.id.indexOf(key) > -1
            || bean.ticker.indexOf(key) > -1
            || bean.name.indexOfIgnoreCase(key)
            || bean.cnShortSpell.indexOfIgnoreCase(key);
}

SecurityClientSearch.prototype.doBeforeCall = function() {

}

SecurityClientSearch.prototype.doAfterCall = function() {

}

SecurityClientSearch.prototype.displayLayer = function(logic) {
    var _style = this.layerObject.style;
    var site = this.tempObject;
    if (logic) {
        var setTop = this.tempObject.offsetTop;
        var setLeft = this.tempObject.offsetLeft;
        while (site = site.offsetParent) {
            setTop += site.offsetTop;
            setLeft += site.offsetLeft;
        }
        _style.left = setLeft;
        _style.top = setTop + 18;
        _style.display = "";
        _style.visibility = "visible";
    } else {
        _style.display = "none";
        _style.visibility = "hidden";
    }
}

SecurityClientSearch.prototype.keyUp = function(event) {
    if (event.keyCode == 40 || event.keyCode == 38) {

    }
    else if (event.keyCode == 27) {   //Esc
        this.lastTR = null;
        this.displayLayer(false);
    }
    else if (event.keyCode == 13) {    //enter
        if (this.layerObject.style.display == 'none' || this.layerObject.style.visibility == 'hidden') {
            return;
        }
        var tables = this.layerObject.getElementsByTagName("TABLE");
        var tableRows = null;
        if (tables.length > 0) tableRows = tables[0].rows;
        if (tableRows.length == 1) this.lastTR = tableRows[0];
        if (this.lastTR != null) {
            this.tempObject.value = this.lastTR.getAttribute("id");
            this.targetObject.value = this.lastTR.getAttribute("id");
            this.displayLayer(false);
            this.doAfterCall();
        }
    }
    else {  //search
        this.lastTR = null;
        this.displayLayer(false);
        this.search();
    }
}

SecurityClientSearch.prototype.keyDown = function(event) {
    if (event.keyCode == 40) { //keydown
        if (this.lastTR != null) {
            this.lastTR.className = this.lastTR.className.replace(/\bsearch_over\b/g, '');
            var _parentNode = this.lastTR.parentNode;
            this.lastTR = this.lastTR.rowIndex == (_parentNode.rows.length - 1) ? _parentNode.rows[0] : _parentNode.rows[this.lastTR.rowIndex + 1];
            this.lastTR.className += ' search_over';
            if (this.lastTR.rowIndex >= this.displaySize) {
                this.layerObject.scrollTop += this.rowHeight;
            }
            if (this.lastTR.rowIndex == 0) {
                this.layerObject.scrollTop = 0;
            }
        } else {
            if (this.layerObject.style.display == 'none' || this.layerObject.style.visibility == 'hidden') {
                return;
            }
            var tables = this.layerObject.getElementsByTagName("TABLE");
            var tableRows = null;
            if (tables.length > 0) tableRows = tables[0].rows;
            if (tableRows.length > 0) this.lastTR = tableRows[0];
            if (this.lastTR != null) {
                if (this.lastTR.rowIndex >= this.displaySize) {
                    this.layerObject.scrollTop += this.rowHeight;
                }
                if (this.lastTR.rowIndex == 0) {
                    this.layerObject.scrollTop = 0;
                }
                var _clicked = this.lastTR.className.match(/\bsearch_over\b/);
                if (_clicked == null) {
                    this.lastTR.className += ' search_over';
                }
            }
        }
    }
    else if (event.keyCode == 38) {   //keyup
        if (this.lastTR != null) {
            this.lastTR.className = this.lastTR.className.replace(/\bsearch_over\b/g, '');
            _parentNode = this.lastTR.parentNode;
            this.lastTR = this.lastTR.rowIndex == 0 ? _parentNode.rows[_parentNode.rows.length - 1] : _parentNode.rows[this.lastTR.rowIndex - 1];
            this.lastTR.className += ' search_over';
            if (this.lastTR.rowIndex >= this.displaySize - 1) {
                this.layerObject.scrollTop -= this.rowHeight;
            }
            if (this.lastTR.rowIndex == _parentNode.rows.length - 1) {
                this.layerObject.scrollTop = _parentNode.rows.length * this.rowHeight;
            }
        } else {
            if (this.layerObject.style.display == 'none' || this.layerObject.style.visibility == 'hidden') {
                return;
            }
            tables = this.layerObject.getElementsByTagName("TABLE");
            tableRows = null;
            if (tables.length > 0) tableRows = tables[0].rows;
            if (tableRows.length > 0) this.lastTR = tableRows[tableRows.length - 1];
            if (this.lastTR != null) {
                if (this.lastTR.rowIndex >= this.displaySize - 1) {
                    this.layerObject.scrollTop -= this.rowHeight;
                }
                if (this.lastTR.rowIndex == tableRows.length - 1) {
                    this.layerObject.scrollTop = tableRows.length * this.rowHeight;
                }
                _clicked = this.lastTR.className.match(/\bsearch_over\b/);
                if (_clicked == null) {
                    this.lastTR.className += ' search_over';
                }
            }
        }
    }
    else if (this.disableEnter && event.keyCode == 13) {
        event.returnValue = false;
    }
}

SecurityClientSearch.prototype.displayResult = function(result) {
    this.layerObject.innerHTML = "";
    if (result == null || result.length == 0) {
        this.targetObject.value = "";
//            this.giveHint("result not found!");
        return;
    }
    var resultStr = "<input type='hidden' name='" + this.inputName + "div' />";
    resultStr += "<iframe style=\"position: absolute;width:100%; z-index: -1; filter: alpha(opacity=0);height:" + (this.rowHeight * result.length + 2) + "px;\"></iframe>";
    resultStr += "<table broder='0' cellspacing='0' cellpadding='0' class='" + this.tableClass + "'>";
    for (var i = 0; i < result.length; i++) {
        var id = result[i].id;
        var ticker = result[i].ticker;
        var name = result[i].name;
        resultStr += "<tr id='" + id + "' class='" + (i % 2 == 0 ? "search_odd" : "search_even")
                + "' onmouseover='SecurityClientSearch.mouseover(this);' onmouseout='javascript:SecurityClientSearch.mouseout(this);'" +
                     " onclick='javascript:SecurityClientSearch.click(this,\"" + this.formName + "\",\"" + this.tempName + "\",\"" + this.inputName + "\");' >";
        resultStr += "<td><strong>" + (i + 1) + "</strong></td><td>" + ticker + "</td><td>" + name + "</td></tr>";
    }
    resultStr += "</table>";
    if (result.length > this.displaySize) {
        this.layerObject.style.height = (this.rowHeight * this.displaySize ) + "px";
    } else {
        this.layerObject.style.height = (this.rowHeight * result.length + 2) + "px";
    }
    this.layerObject.innerHTML = resultStr;
    this.displayLayer(true);
}

SecurityClientSearch.prototype.giveHint = function(s) {
    var obj = this.hintId == null ? null : document.getElementById(this.hintId);
    if (obj == null) {
        alert(s);
    } else {
        obj.innerHTML = s;
    }
}

SecurityClientSearch.prototype.clearHint = function() {
    var obj = this.hintId == null ? null : document.getElementById(this.hintId);
    if (obj != null) {
        obj.innerHTML = "";
    }
}

SecurityClientSearch.prototype.addClickEvent = function() {
    var oldonclick = document.body.onclick;
    var tempObj = this.tempObject;
    var layerObj = this.layerObject;
    var callBackFunc = this.doAfterCall;
    if (typeof oldonclick != 'function') {
        document.body.onclick = function(e) {
            var obj = document.all ? (event ? event.srcElement : null) : (e.target);
            if (layerObj.contains(obj)) {
                callBackFunc();
            } else if (obj != tempObj) {
                hiddenLayer(layerObj);
            }
        }
    }
    else {
        document.body.onclick = function(e) {
            oldonclick(e);
            var obj2 = document.all ? (event ? event.srcElement : null) : (e.target);
            if (layerObj.contains(obj2)) {
                callBackFunc();
            } else if (obj2 != tempObj) {
                hiddenLayer(layerObj);
            }
        }
    }
}

SecurityClientSearch.click = function(tr, fm, tempName, targetName) {
    hiddenLayer(getElement(fm, targetName + "div").parentNode);
    getElement(fm, tempName).value = tr.getAttribute("id");
    getElement(fm, targetName).value = tr.getAttribute("id");
}

SecurityClientSearch.mouseover = function(tr) {
    var _clicked = tr.className.match(/\bsearch_over\b/);
    if (_clicked == null) {
        tr.className += ' search_over';
    }
}

SecurityClientSearch.mouseout = function(tr) {
    tr.className = tr.className.replace(/\bsearch_over\b/g, '');
}

function MultiSecuritySearch(contextPath, url, formName, selectName, tableClass, hintId, displaySize, maxSize,maxSelectSize, disableEnter) {
    this.baseUrl = url;
    this.formName = formName;
    this.selectName = selectName;
    this.tableClass = tableClass;
    this.hintId = hintId;
    this.maxSize = maxSize == -1 ? 100 : maxSize;
    this.disableEnter = disableEnter;
    this.tempName = "temp" + this.selectName;
    this.layerObject = getElement(this.formName, this.selectName + "div").parentNode;
    this.tempObject = getElement(this.formName, this.tempName);
    this.targetObject = getElement(this.formName, this.selectName);
    this.displayObject = getElement(this.formName, this.selectName + "displaydiv").parentNode;
    this.request = null;
    this.displaySize = displaySize;
    this.rowHeight = 23;
    this.inited = false;
    this.waitingMillSec = 50;
    this.keyUpTime = null;
    this.closeIcon = contextPath + "/images/closeNode.gif";
    this.openIcon = contextPath + "/images/openNode.gif";
    this.deleteIcon = contextPath + "/images/deleteNode.gif";
    this.maxSelectSize = maxSelectSize;
}

MultiSecuritySearch.prototype.getParams = function() {
    return "&maxCount=" + this.maxSize + "&key=" + encode64(escape(this.tempObject.value));
}

MultiSecuritySearch.prototype.search = function() {
    if (!this.inited) {
        this.addClickEvent();
        this.inited = true;
    }
    this.clearHint();
    if (!this.tempObject.value.isEmpty()) {
        this.connect();
    }
}

MultiSecuritySearch.prototype.connect = function() {
    this.keyUpTime = (new Date()).getTime();
    setTimeout("this.doSearch()", this.waitingMillSec);
}

MultiSecuritySearch.prototype.doSearch = function() {
    var nowTime = (new Date()).getTime();
    if (nowTime - this.keyUpTime < this.waitingMillSec) return;
    this.doBeforeCall();
    this.request = this.getRequest();
    if (this.request) {
        var data = this.getParams();
        this.request.open('POST', this.baseUrl, true);
        this.request.setRequestHeader("content-length", data.length);
        this.request.setRequestHeader("content-type", "application/x-www-form-urlencoded");
        this.request.onreadystatechange = this.getResult;
        this.request.send(data);
    }
}

MultiSecuritySearch.prototype.doBeforeCall = function() {

}

MultiSecuritySearch.prototype.doAfterCall = function() {

}

MultiSecuritySearch.prototype.displayLayer = function(logic) {
    var _style = this.layerObject.style;
    var site = this.tempObject;
    if (logic) {
        var setTop = this.tempObject.offsetTop;
        var setLeft = this.tempObject.offsetLeft;
        while (site = site.offsetParent) {
            setTop += site.offsetTop;
            setLeft += site.offsetLeft;
        }
        _style.left = setLeft;
        _style.top = setTop + 18;
        _style.display = "";
        _style.visibility = "visible";
    } else {
        _style.display = "none";
        _style.visibility = "hidden";
    }
}

MultiSecuritySearch.prototype.keyUp = function(event) {
    if (event.keyCode == 40 || event.keyCode == 38) {

    }
    else if (event.keyCode == 27) {   //Esc
        this.lastTR = null;
        this.displayLayer(false);
    }
    else if (event.keyCode == 13) {    //enter
        if (this.layerObject.style.display == 'none' || this.layerObject.style.visibility == 'hidden') {
            return;
        }
        var tables = this.layerObject.getElementsByTagName("TABLE");
        var tableRows = null;
        if (tables.length > 0) tableRows = tables[0].rows;
        if (tableRows.length == 1) this.lastTR = tableRows[0];
        if (this.lastTR != null) {
            this.tempObject.value = "";
            var len = this.targetObject.options.length;
            var isExist = false;
            var id = this.lastTR.getAttribute("id");
            if (this.maxSelectSize != -1 && len >= this.maxSelectSize) {
                alert("超出最大限制：" + this.maxSelectSize);
                isExist = true;
            } else {
                for (var i = 0; i < len; i++) {
                    if (this.targetObject.options[i].value == id) {
                        isExist = true;
                        break;
                    }
                }
            }
            if (!isExist) {
                this.targetObject.options[len] = new Option(this.lastTR.cells[1].innerHTML + " " + this.lastTR.cells[2].innerHTML, id);
                this.targetObject.options[len].selected = true;
            }
            this.displayLayer(false);
            this.doView(2);
            this.doAfterCall();
        }
    }
    else {  //search
        this.lastTR = null;
        this.displayLayer(false);
        this.search();
    }
}

MultiSecuritySearch.prototype.keyDown = function(event) {
    if (event.keyCode == 40) { //keydown
        if (this.lastTR != null) {
            this.lastTR.className = this.lastTR.className.replace(/\bsearch_over\b/g, '');
            var _parentNode = this.lastTR.parentNode;
            this.lastTR = this.lastTR.rowIndex == (_parentNode.rows.length - 1) ? _parentNode.rows[0] : _parentNode.rows[this.lastTR.rowIndex + 1];
            this.lastTR.className += ' search_over';
            if (this.lastTR.rowIndex >= this.displaySize) {
                this.layerObject.scrollTop += this.rowHeight;
            }
            if (this.lastTR.rowIndex == 0) {
                this.layerObject.scrollTop = 0;
            }
        } else {
            if (this.layerObject.style.display == 'none' || this.layerObject.style.visibility == 'hidden') {
                return;
            }
            var tables = this.layerObject.getElementsByTagName("TABLE");
            var tableRows = null;
            if (tables.length > 0) tableRows = tables[0].rows;
            if (tableRows.length > 0) this.lastTR = tableRows[0];
            if (this.lastTR != null) {
                if (this.lastTR.rowIndex >= this.displaySize) {
                    this.layerObject.scrollTop += this.rowHeight;
                }
                if (this.lastTR.rowIndex == 0) {
                    this.layerObject.scrollTop = 0;
                }
                var _clicked = this.lastTR.className.match(/\bsearch_over\b/);
                if (_clicked == null) {
                    this.lastTR.className += ' search_over';
                }
            }
        }
    }
    else if (event.keyCode == 38) {   //keyup
        if (this.lastTR != null) {
            this.lastTR.className = this.lastTR.className.replace(/\bsearch_over\b/g, '');
            _parentNode = this.lastTR.parentNode;
            this.lastTR = this.lastTR.rowIndex == 0 ? _parentNode.rows[_parentNode.rows.length - 1] : _parentNode.rows[this.lastTR.rowIndex - 1];
            this.lastTR.className += ' search_over';
            if (this.lastTR.rowIndex >= this.displaySize - 1) {
                this.layerObject.scrollTop -= this.rowHeight;
            }
            if (this.lastTR.rowIndex == _parentNode.rows.length - 1) {
                this.layerObject.scrollTop = _parentNode.rows.length * this.rowHeight;
            }
        } else {
            if (this.layerObject.style.display == 'none' || this.layerObject.style.visibility == 'hidden') {
                return;
            }
            tables = this.layerObject.getElementsByTagName("TABLE");
            tableRows = null;
            if (tables.length > 0) tableRows = tables[0].rows;
            if (tableRows.length > 0) this.lastTR = tableRows[tableRows.length - 1];
            if (this.lastTR != null) {
                if (this.lastTR.rowIndex >= this.displaySize - 1) {
                    this.layerObject.scrollTop -= this.rowHeight;
                }
                if (this.lastTR.rowIndex == tableRows.length - 1) {
                    this.layerObject.scrollTop = tableRows.length * this.rowHeight;
                }
                _clicked = this.lastTR.className.match(/\bsearch_over\b/);
                if (_clicked == null) {
                    this.lastTR.className += ' search_over';
                }
            }
        }
    }
    else if (this.disableEnter && event.keyCode == 13) {
        event.cancelBubble = true;
        event.returnValue = false;
    }
}

MultiSecuritySearch.prototype.displayResult = function(xmlDom) {
    this.layerObject.innerHTML = "";
    if (xmlDom == null) {
        //            this.giveHint("result not found!");
        return;
    }
    var dataRoot = this.getNode(xmlDom, "dataRoot");
    var dataItem = this.getNode(dataRoot[0], "dataItem");
    if (dataItem.length == 0) {
        //            this.giveHint("result not found!");
        return;
    }
    var resultStr = "<input type='hidden' name='" + this.selectName + "div' />";
    resultStr += "<iframe style=\"position: absolute;width:100%; z-index: -1; filter: alpha(opacity=0);height:" + (this.rowHeight * dataItem.length + 2) + "px;\"></iframe>";
    resultStr += "<table broder='0' cellspacing='0' cellpadding='0' class='" + this.tableClass + "'>";
    for (var i = 0; i < dataItem.length; i++) {
        var id = this.getNodeValue(this.getNode(dataItem[i], "id"), 0);
        var ticker = this.getNodeValue(this.getNode(dataItem[i], "ticker"), 0);
        var name = this.getNodeValue(this.getNode(dataItem[i], "name"), 0);
        resultStr += "<tr id='" + id + "' class='" + (i % 2 == 0 ? "search_odd" : "search_even")
                + "' onmouseover='MultiSecuritySearch.mouseover(this);' onmouseout='javascript:MultiSecuritySearch.mouseout(this);'" +
                     " onclick=\"javascript:MultiSecuritySearch.click(this,'" + this.formName + "','" + this.tempName + "','" + this.selectName + "','" + this.rowHeight + "','" + this.deleteIcon + "','" + this.tableClass + "'," + this.maxSelectSize + ");\" >";
        resultStr += "<td><strong>" + (i + 1) + "</strong></td><td>" + ticker + "</td><td>" + name + "</td></tr>";
    }
    resultStr += "</table>";
    if (dataItem.length > this.displaySize) {
        this.layerObject.style.height = (this.rowHeight * this.displaySize ) + "px";
    } else {
        this.layerObject.style.height = (this.rowHeight * dataItem.length + 2) + "px";
    }
    this.layerObject.innerHTML = resultStr;
    this.displayLayer(true);
}

MultiSecuritySearch.prototype.getResult = function() {
    if (this.request.readyState == 4) {
        if (this.request.status == 200) {
            this.displayResult(this.request.responseXML);
            this.request.abort();
            this.request = null;
        }
    }
}

MultiSecuritySearch.prototype.getNode = function(obj, tagName) {
    return obj.getElementsByTagName(tagName);
}

MultiSecuritySearch.prototype.getNodeValue = function(node, site) {
    var nodeValue = node.item(site).firstChild.nodeValue;
    return decodeURIComponent((nodeValue));
}

MultiSecuritySearch.prototype.encoder = function(str) {
    return encodeURI(str);
}

MultiSecuritySearch.prototype.getRequest = function() {
    try {
        if (window.XMLHttpRequest) {
            return new XMLHttpRequest();
        } else if (window.ActiveXObject) {
            return new ActiveXObject("Microsoft.XMLHTTP");
        } else {
            this.giveHint("您使用的浏览支持XMLHttpRequest，请使用IE6.0！");
        }
    } catch(e) {
        this.giveHint(e);
        this.giveHint("您可能限制浏览器访问某些安全控件，\n请点击浏览器上方的[工具]-->[INTERNET选项]-->[安全]-->选择[默认级别]，点击[确定]");
    }
    return null;
}

MultiSecuritySearch.prototype.doDisplayAction = function(obj) {
    var _src = obj.src;
    if (_src.indexOf(this.openIcon) != -1) {
        obj.src = this.closeIcon;
        obj.alt = "收起列表";
        this.doView(1);
    } else {
        obj.src = this.openIcon;
        obj.alt = "展开列表";
        this.doView(0);
    }
}

MultiSecuritySearch.prototype.doView = function(type) {
    var resultStr = "<input type='hidden' name='" + this.selectName + "displaydiv' />";
    resultStr += "<table broder='0' cellspacing='0' cellpadding='0' class='" + this.tableClass + "'>";
    var len = 0;
    len = this.targetObject.options.length;
    for (var i = 0; i < len; i++) {
        var id = this.targetObject.options[i].value;
        resultStr += "<tr class=" + (i % 2 == 0 ? "'search_odd'" : "'search_even'") + ">";
        resultStr += "<td><strong>" + (i + 1) + "</strong></td><td>" + this.targetObject.options[i].text + "</td>";
        resultStr += "<td><img src='" + this.deleteIcon + "' style='width:16px;height:16px;cursor:pointer' border='0' align='absMiddle' alt='删除'" +
                     " onclick=\"MultiSecuritySearch.doDelete('" + id + "','" + this.formName + "','" + this.selectName + "','" + this.rowHeight + "','" + this.deleteIcon + "','" + this.tableClass + "');\"/></td></tr>";
    }
    resultStr += "</table>";
    this.displayObject.style.height = (this.rowHeight * len + 2) + "px";
    this.displayObject.innerHTML = resultStr;
    var _style = this.displayObject.style;
    if (type == 1) {
        _style.display = "";
        _style.visibility = "visible";
    } else if (type == 0) {
        _style.display = "none";
        _style.visibility = "hidden";
    }
}

MultiSecuritySearch.prototype.giveHint = function(s) {
    var obj = this.hintId == null ? null : document.getElementById(this.hintId);
    if (obj == null) {
        alert(s);
    } else {
        obj.innerHTML = s;
    }
}

MultiSecuritySearch.prototype.clearHint = function() {
    var obj = this.hintId == null ? null : document.getElementById(this.hintId);
    if (obj != null) {
        obj.innerHTML = "";
    }
}

MultiSecuritySearch.prototype.addClickEvent = function() {
    var oldonclick = document.body.onclick;
    var tempObj = this.tempObject;
    var layerObj = this.layerObject;
    var callBackFunc = this.doAfterCall;
    if (typeof oldonclick != 'function') {
        document.body.onclick = function(e) {
            var obj = document.all ? (event ? event.srcElement : null) : (e.target);
            if (layerObj.contains(obj)) {
                callBackFunc();
            } else if (obj != tempObj) {
                hiddenLayer(layerObj);
            }
        }
    }
    else {
        document.body.onclick = function(e) {
            oldonclick(e);
            var obj2 = document.all ? (event ? event.srcElement : null) : (e.target);
            if (layerObj.contains(obj2)) {
                callBackFunc();
            } else if (obj2 != tempObj) {
                hiddenLayer(layerObj);
            }
        }
    }
}

MultiSecuritySearch.click = function(tr, fm, tempName, targetName, rowHight, deleteIcon, tableClass, maxSelectSize) {
    hiddenLayer(getElement(fm, targetName + "div").parentNode);
    getElement(fm, tempName).value = "";
    var obj = getElement(fm, targetName);
    var len = obj.options.length;
    var id = tr.getAttribute("id");
    var isExist = false;
    if (maxSelectSize != -1 && len >= maxSelectSize) {
        alert("超出最大限制：" + maxSelectSize);
        isExist = true;
    } else {
        for (var i = 0; i < len; i++) {
            if (obj.options[i].value == id) {
                isExist = true;
                break;
            }
        }
    }
    if (!isExist) {
        obj.options[len] = new Option(tr.cells[1].innerHTML + " " + tr.cells[2].innerHTML, id);
        obj.options[len].selected = true;
    }
    var displayObject = getElement(fm, targetName + "displaydiv").parentNode
    var resultStr = "<input type='hidden' name='" + targetName + "displaydiv' />";
    resultStr += "<table broder='0' cellspacing='0' cellpadding='0' class='" + tableClass + "'>";
    len = obj.options.length;
    for (i = 0; i < len; i++) {
        id = obj.options[i].value;
        resultStr += "<tr class=" + (i % 2 == 0 ? "'search_odd'" : "'search_even'") + ">";
        resultStr += "<td><strong>" + (i + 1) + "</strong></td><td>" + obj.options[i].text + "</td>";
        resultStr += "<td><img src='" + deleteIcon + "' style='width:16px;height:16px;cursor:pointer' border='0' align='absMiddle' alt='删除'" +
                     " onclick=\"MultiSecuritySearch.doDelete('" + id + "','" + fm + "','" + targetName + "','" + rowHight + "','" + deleteIcon + "','" + tableClass + "');\"/></td></tr>";
    }
    resultStr += "</table>";
    displayObject.style.height = (rowHight * len + 2) + "px";
    displayObject.innerHTML = resultStr;
}

MultiSecuritySearch.mouseover = function(tr) {
    var _clicked = tr.className.match(/\bsearch_over\b/);
    if (_clicked == null) {
        tr.className += ' search_over';
    }
}

MultiSecuritySearch.mouseout = function(tr) {
    tr.className = tr.className.replace(/\bsearch_over\b/g, '');
}

MultiSecuritySearch.doDelete = function(id, fm, targetName, rowHight, deleteIcon, tableClass) {
    var obj = getElement(fm, targetName);
    var len = obj.options.length;
    var index = len;
    for (var i = 0; i < len; i++) {
        if (obj.options[i].value == id) {
            index = i;
            break;
        }
    }
    for (i = index; i < len - 1; i++) {
        var text = obj.options[i + 1].text;
        var value = obj.options[i + 1].value;
        obj.options[i] = new Option(text, value);
        obj.options[i].selected = true;
    }
    if (index < len) {
        obj.options[len - 1].selected = false;
        obj.options.length = len - 1;
    }
    var displayObject = getElement(fm, targetName + "displaydiv").parentNode
    var resultStr = "<input type='hidden' name='" + targetName + "displaydiv' />";
    resultStr += "<table broder='0' cellspacing='0' cellpadding='0' class='" + tableClass + "'>";
    len = obj.options.length;
    for (i = 0; i < len; i++) {
        id = obj.options[i].value;
        resultStr += "<tr class=" + (i % 2 == 0 ? "'search_odd'" : "'search_even'") + ">";
        resultStr += "<td><strong>" + (i + 1) + "</strong></td><td>" + obj.options[i].text + "</td>";
        resultStr += "<td><img src='" + deleteIcon + "' style='width:16px;height:16px;cursor:pointer' border='0' align='absMiddle' alt='删除'" +
                     " onclick=\"MultiSecuritySearch.doDelete('" + id + "','" + fm + "','" + targetName + "','" + rowHight + "','" + deleteIcon + "','" + tableClass + "');\"/></td></tr>";
    }
    resultStr += "</table>";
    displayObject.style.height = (rowHight * len + 2) + "px";
    displayObject.innerHTML = resultStr;
}

function MultiSecurityClientSearch(contextPath, sourceArray, formName, selectName, tableClass, hintId, displaySize, maxSize,maxSelectSize,disableEnter) {
    this.sourceArray = sourceArray;
    this.formName = formName;
    this.selectName = selectName;
    this.tableClass = tableClass;
    this.hintId = hintId;
    this.maxSize = maxSize == -1 ? 100 : maxSize;
    this.disableEnter = disableEnter;
    this.tempName = "temp" + this.selectName;
    this.layerObject = getElement(this.formName, this.selectName + "div").parentNode;
    this.tempObject = getElement(this.formName, this.tempName);
    this.targetObject = getElement(this.formName, this.selectName);
    this.displayObject = getElement(this.formName, this.selectName + "displaydiv").parentNode;
    this.displaySize = displaySize;
    this.rowHeight = 23;
    this.inited = false;
    this.closeIcon = contextPath + "/images/closeNode.gif";
    this.openIcon = contextPath + "/images/openNode.gif";
    this.deleteIcon = contextPath + "/images/deleteNode.gif";
    this.maxSelectSize = maxSelectSize;
}

MultiSecurityClientSearch.prototype.search = function() {
    if (!this.inited) {
        this.addClickEvent();
        this.inited = true;
    }
    this.clearHint();
    if (this.tempObject.value.isEmpty()) {
        return;
    }
    var key = this.tempObject.value.trim();
    this.doBeforeCall();
    var result = new Array();
    var len = 0;
    for (var i = 0; i < this.sourceArray.length; i++) {
        if (this.isValid(key, this.sourceArray[i])) {
            result[len] = this.sourceArray[i];
            len++;
        }
    }
    this.displayResult(result);
}

MultiSecurityClientSearch.prototype.isValid = function(key, bean) {
    return bean.id.indexOf(key) > -1
            || bean.ticker.indexOf(key) > -1
            || bean.name.indexOfIgnoreCase(key)
            || bean.cnShortSpell.indexOfIgnoreCase(key);
}

MultiSecurityClientSearch.prototype.doBeforeCall = function() {

}

MultiSecurityClientSearch.prototype.doAfterCall = function() {

}

MultiSecurityClientSearch.prototype.displayLayer = function(logic) {
    var _style = this.layerObject.style;
    var site = this.tempObject;
    if (logic) {
        var setTop = this.tempObject.offsetTop;
        var setLeft = this.tempObject.offsetLeft;
        while (site = site.offsetParent) {
            setTop += site.offsetTop;
            setLeft += site.offsetLeft;
        }
        _style.left = setLeft;
        _style.top = setTop + 18;
        _style.display = "";
        _style.visibility = "visible";
    } else {
        _style.display = "none";
        _style.visibility = "hidden";
    }
}

MultiSecurityClientSearch.prototype.keyUp = function(event) {
    if (event.keyCode == 40 || event.keyCode == 38) {

    }
    else if (event.keyCode == 27) {   //Esc
        this.lastTR = null;
        this.displayLayer(false);
    }
    else if (event.keyCode == 13) {    //enter
        if (this.layerObject.style.display == 'none' || this.layerObject.style.visibility == 'hidden') {
            return;
        }
        var tables = this.layerObject.getElementsByTagName("TABLE");
        var tableRows = null;
        if (tables.length > 0) tableRows = tables[0].rows;
        if (tableRows.length == 1) this.lastTR = tableRows[0];
        if (this.lastTR != null) {
            this.tempObject.value = "";
            var len = this.targetObject.options.length;
            var isExist = false;
            var id = this.lastTR.getAttribute("id");
            if (this.maxSelectSize != -1 && len >= this.maxSelectSize) {
                this.giveHint("超出最大限制：" + this.maxSelectSize);
                isExist = true;
            }
            else {
                for (var i = 0; i < len; i++) {
                    if (this.targetObject.options[i].value == id) {
                        isExist = true;
                        break;
                    }
                }
            }
            if (!isExist) {
                this.targetObject.options[len] = new Option(this.lastTR.cells[1].innerHTML + " " + this.lastTR.cells[2].innerHTML, id);
                this.targetObject.options[len].selected = true;
            }
            this.displayLayer(false);
            this.doView(2);
            this.doAfterCall();
        }
    }
    else {  //search
        this.lastTR = null;
        this.displayLayer(false);
        this.search();
    }
}

MultiSecurityClientSearch.prototype.keyDown = function(event) {
    if (event.keyCode == 40) { //keydown
        if (this.lastTR != null) {
            this.lastTR.className = this.lastTR.className.replace(/\bsearch_over\b/g, '');
            var _parentNode = this.lastTR.parentNode;
            this.lastTR = this.lastTR.rowIndex == (_parentNode.rows.length - 1) ? _parentNode.rows[0] : _parentNode.rows[this.lastTR.rowIndex + 1];
            this.lastTR.className += ' search_over';
            if (this.lastTR.rowIndex >= this.displaySize) {
                this.layerObject.scrollTop += this.rowHeight;
            }
            if (this.lastTR.rowIndex == 0) {
                this.layerObject.scrollTop = 0;
            }
        } else {
            if (this.layerObject.style.display == 'none' || this.layerObject.style.visibility == 'hidden') {
                return;
            }
            var tables = this.layerObject.getElementsByTagName("TABLE");
            var tableRows = null;
            if (tables.length > 0) tableRows = tables[0].rows;
            if (tableRows.length > 0) this.lastTR = tableRows[0];
            if (this.lastTR != null) {
                if (this.lastTR.rowIndex >= this.displaySize) {
                    this.layerObject.scrollTop += this.rowHeight;
                }
                if (this.lastTR.rowIndex == 0) {
                    this.layerObject.scrollTop = 0;
                }
                var _clicked = this.lastTR.className.match(/\bsearch_over\b/);
                if (_clicked == null) {
                    this.lastTR.className += ' search_over';
                }
            }
        }
    }
    else if (event.keyCode == 38) {   //keyup
        if (this.lastTR != null) {
            this.lastTR.className = this.lastTR.className.replace(/\bsearch_over\b/g, '');
            _parentNode = this.lastTR.parentNode;
            this.lastTR = this.lastTR.rowIndex == 0 ? _parentNode.rows[_parentNode.rows.length - 1] : _parentNode.rows[this.lastTR.rowIndex - 1];
            this.lastTR.className += ' search_over';
            if (this.lastTR.rowIndex >= this.displaySize - 1) {
                this.layerObject.scrollTop -= this.rowHeight;
            }
            if (this.lastTR.rowIndex == _parentNode.rows.length - 1) {
                this.layerObject.scrollTop = _parentNode.rows.length * this.rowHeight;
            }
        } else {
            if (this.layerObject.style.display == 'none' || this.layerObject.style.visibility == 'hidden') {
                return;
            }
            tables = this.layerObject.getElementsByTagName("TABLE");
            tableRows = null;
            if (tables.length > 0) tableRows = tables[0].rows;
            if (tableRows.length > 0) this.lastTR = tableRows[tableRows.length - 1];
            if (this.lastTR != null) {
                if (this.lastTR.rowIndex >= this.displaySize - 1) {
                    this.layerObject.scrollTop -= this.rowHeight;
                }
                if (this.lastTR.rowIndex == tableRows.length - 1) {
                    this.layerObject.scrollTop = tableRows.length * this.rowHeight;
                }
                _clicked = this.lastTR.className.match(/\bsearch_over\b/);
                if (_clicked == null) {
                    this.lastTR.className += ' search_over';
                }
            }
        }
    }
    else if (this.disableEnter && event.keyCode == 13) {
        event.cancelBubble = true;
        event.returnValue = false;
    }
}

MultiSecurityClientSearch.prototype.displayResult = function(resultArray) {
    this.layerObject.innerHTML = "";
    if (resultArray == null || resultArray.length == 0) {
        //            this.giveHint("result not found!");
        return;
    }
    var resultStr = "<input type='hidden' name='" + this.selectName + "div' />";
    resultStr += "<iframe style=\"position: absolute;width:100%; z-index: -1; filter: alpha(opacity=0);height:" + (this.rowHeight * resultArray.length + 2) + "px;\"></iframe>";
    resultStr += "<table broder='0' cellspacing='0' cellpadding='0' class='" + this.tableClass + "'>";
    for (var i = 0; i < resultArray.length; i++) {
        var id = resultArray[i].id;
        var ticker = resultArray[i].ticker;
        var name = resultArray[i].name;
        resultStr += "<tr id='" + id + "' class='" + (i % 2 == 0 ? "search_odd" : "search_even")
                + "' onmouseover='MultiSecurityClientSearch.mouseover(this);' onmouseout='javascript:MultiSecurityClientSearch.mouseout(this);'" +
                     " onclick=\"javascript:MultiSecurityClientSearch.click(this,'" + this.formName + "','" + this.tempName + "','" + this.selectName + "','" + this.rowHeight + "','" + this.deleteIcon + "','" + this.tableClass + "'," + this.maxSelectSize + ");\" >";
        resultStr += "<td><strong>" + (i + 1) + "</strong></td><td>" + ticker + "</td><td>" + name + "</td></tr>";
    }
    resultStr += "</table>";
    if (resultArray.length > this.displaySize) {
        this.layerObject.style.height = (this.rowHeight * this.displaySize ) + "px";
    }
    else {
        this.layerObject.style.height = (this.rowHeight * resultArray.length + 2) + "px";
    }
    this.layerObject.innerHTML = resultStr;
    this.displayLayer(true);
}

MultiSecurityClientSearch.prototype.doDisplayAction = function(obj) {
    var _src = obj.src;
    if (_src.indexOf(this.openIcon) != -1) {
        obj.src = this.closeIcon;
        obj.alt = "收起列表";
        this.doView(1);
    } else {
        obj.src = this.openIcon;
        obj.alt = "展开列表";
        this.doView(0);
    }
}

MultiSecurityClientSearch.prototype.doView = function(type) {
    var resultStr = "<input type='hidden' name='" + this.selectName + "displaydiv' />";
    resultStr += "<table broder='0' cellspacing='0' cellpadding='0' class='" + this.tableClass + "'>";
    var len = 0;
    len = this.targetObject.options.length;
    for (var i = 0; i < len; i++) {
        var id = this.targetObject.options[i].value;
        resultStr += "<tr class=" + (i % 2 == 0 ? "'search_odd'" : "'search_even'") + ">";
        resultStr += "<td><strong>" + (i + 1) + "</strong></td><td>" + this.targetObject.options[i].text + "</td>";
        resultStr += "<td><img src='" + this.deleteIcon + "' style='width:16px;height:16px;cursor:pointer' border='0' align='absMiddle' alt='删除'" +
                     " onclick=\"MultiSecurityClientSearch.doDelete('" + id + "','" + this.formName + "','" + this.selectName + "','" + this.rowHeight + "','" + this.deleteIcon + "','" + this.tableClass + "');\"/></td></tr>";
    }
    resultStr += "</table>";
    this.displayObject.style.height = (this.rowHeight * len + 2) + "px";
    this.displayObject.innerHTML = resultStr;
    var _style = this.displayObject.style;
    if (type == 1) {
        _style.display = "";
        _style.visibility = "visible";
    } else if (type == 0) {
        _style.display = "none";
        _style.visibility = "hidden";
    }
}

MultiSecurityClientSearch.prototype.giveHint = function(s) {
    var obj = this.hintId == null ? null : document.getElementById(this.hintId);
    if (obj == null) {
        alert(s);
    } else {
        obj.innerHTML = s;
    }
}

MultiSecurityClientSearch.prototype.clearHint = function() {
    var obj = this.hintId == null ? null : document.getElementById(this.hintId);
    if (obj != null) {
        obj.innerHTML = "";
    }
}

MultiSecurityClientSearch.prototype.addClickEvent = function() {
    var oldonclick = document.body.onclick;
    var tempObj = this.tempObject;
    var layerObj = this.layerObject;
    var callBackFunc = this.doAfterCall;
    if (typeof oldonclick != 'function') {
        document.body.onclick = function(e) {
            var obj = document.all ? (event ? event.srcElement : null) : (e.target);
            if (layerObj.contains(obj)) {
                callBackFunc();
            } else if (obj != tempObj) {
                hiddenLayer(layerObj);
            }
        }
    }
    else {
        document.body.onclick = function(e) {
            oldonclick(e);
            var obj2 = document.all ? (event ? event.srcElement : null) : (e.target);
            if (layerObj.contains(obj2)) {
                callBackFunc();
            } else if (obj2 != tempObj) {
                hiddenLayer(layerObj);
            }
        }
    }
}

MultiSecurityClientSearch.click = function(tr, fm, tempName, targetName, rowHight, deleteIcon, tableClass, maxSelectSize) {
    hiddenLayer(getElement(fm, targetName + "div").parentNode);
    getElement(fm, tempName).value = "";
    var obj = getElement(fm, targetName);
    var len = obj.options.length;
    var id = tr.getAttribute("id");
    var isExist = false;
    if (maxSelectSize != -1 && len >= maxSelectSize) {
        alert("超出最大限制：" + maxSelectSize);
        isExist = true;
    } else {
        for (var i = 0; i < len; i++) {
            if (obj.options[i].value == id) {
                isExist = true;
                break;
            }
        }
    }
    if (!isExist) {
        obj.options[len] = new Option(tr.cells[1].innerHTML + " " + tr.cells[2].innerHTML, id);
        obj.options[len].selected = true;
    }
    var displayObject = getElement(fm, targetName + "displaydiv").parentNode
    var resultStr = "<input type='hidden' name='" + targetName + "displaydiv' />";
    resultStr += "<table broder='0' cellspacing='0' cellpadding='0' class='" + tableClass + "'>";
    len = obj.options.length;
    for (i = 0; i < len; i++) {
        id = obj.options[i].value;
        resultStr += "<tr class=" + (i % 2 == 0 ? "'search_odd'" : "'search_even'") + ">";
        resultStr += "<td><strong>" + (i + 1) + "</strong></td><td>" + obj.options[i].text + "</td>";
        resultStr += "<td><img src='" + deleteIcon + "' style='width:16px;height:16px;cursor:pointer' border='0' align='absMiddle' alt='删除'" +
                     " onclick=\"MultiSecurityClientSearch.doDelete('" + id + "','" + fm + "','" + targetName + "','" + rowHight + "','" + deleteIcon + "','" + tableClass + "'," + maxSelectSize + ");\"/></td></tr>";
    }
    resultStr += "</table>";
    displayObject.style.height = (rowHight * len + 2) + "px";
    displayObject.innerHTML = resultStr;
}

MultiSecurityClientSearch.mouseover = function(tr) {
    var _clicked = tr.className.match(/\bsearch_over\b/);
    if (_clicked == null) {
        tr.className += ' search_over';
    }
}

MultiSecurityClientSearch.mouseout = function(tr) {
    tr.className = tr.className.replace(/\bsearch_over\b/g, '');
}

MultiSecurityClientSearch.doDelete = function(id, fm, targetName, rowHight, deleteIcon, tableClass) {
    var obj = getElement(fm, targetName);
    var len = obj.options.length;
    var index = len;
    for (var i = 0; i < len; i++) {
        if (obj.options[i].value == id) {
            index = i;
            break;
        }
    }
    for (i = index; i < len - 1; i++) {
        var text = obj.options[i + 1].text;
        var value = obj.options[i + 1].value;
        obj.options[i] = new Option(text, value);
        obj.options[i].selected = true;
    }
    if (index < len) {
        obj.options[len - 1].selected = false;
        obj.options.length = len - 1;
    }
    var displayObject = getElement(fm, targetName + "displaydiv").parentNode
    var resultStr = "<input type='hidden' name='" + targetName + "displaydiv' />";
    resultStr += "<table broder='0' cellspacing='0' cellpadding='0' class='" + tableClass + "'>";
    len = obj.options.length;
    for (i = 0; i < len; i++) {
        id = obj.options[i].value;
        resultStr += "<tr class=" + (i % 2 == 0 ? "'search_odd'" : "'search_even'") + ">";
        resultStr += "<td><strong>" + (i + 1) + "</strong></td><td>" + obj.options[i].text + "</td>";
        resultStr += "<td><img src='" + deleteIcon + "' style='width:16px;height:16px;cursor:pointer' border='0' align='absMiddle' alt='删除'" +
                     " onclick=\"MultiSecurityClientSearch.doDelete('" + id + "','" + fm + "','" + targetName + "','" + rowHight + "','" + deleteIcon + "','" + tableClass + "');\"/></td></tr>";
    }
    resultStr += "</table>";
    displayObject.style.height = (rowHight * len + 2) + "px";
    displayObject.innerHTML = resultStr;
}

function searchForm(name) {
    var forms = document.forms;
    for (var i = 0; i < forms.length; i++) {
        if (forms[i].name == name) {
            return forms[i];
        }
    }
    return null;
}

function getElement(fmName, eleName) {
    var forms = document.forms;
    for (var i = 0; i < forms.length; i++) {
        if (forms[i].name == fmName) {
            var eles = forms[i].elements;
            for (var j = 0; j < eles.length; j++) {
                if (eles[j].name == eleName
                        || eles[j].getAttribute("name") == eleName
                        || eles[j].getAttribute("id") == eleName) {
                    return eles[j];
                }
            }
        }
    }
    return null;
}

function hiddenLayer(obj) {
    obj.style.display = "none";
    obj.style.visibility = "hidden";
}

{
    String.prototype.toInteger = function() {
        return parseInt(this.replace(/[^\d]/g, ''), 10);
    }

    String.prototype.isEmpty = function() {
        return this.replace(/\s/g, "").length == 0;
    }

    String.prototype.trim = function() {
        return this.replace(/(^\s*)|(\s*$)/g, "");
    }

    String.prototype.indexOfIgnoreCase = function(str) {
        return this.toUpperCase().indexOf(str.toUpperCase()) > -1;
    }

    String.prototype.startWith = function(str) {
        return this.substring(0, str.length) == str;
    }

    String.prototype.endWith = function(str) {
        return this.substring(this.length - str.length) == str;
    }

    String.prototype.startWithIgnoreCase = function(str) {
        return this.substring(0, str.length).toUpperCase() == str.toUpperCase();
    }

    String.prototype.endWithIgnoreCase = function(str) {
        return this.substring(this.length - str.length).toUpperCase() == str.toUpperCase();
    }
}