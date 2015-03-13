var liqlistdisabled=false;

// 切换表格行的样式
function activeRadioTableRow ( tableObj, row ) {
    for ( var i=1; i < tableObj.rows.length; i++ ) {
        if ( tableObj.rows[i].cells[0].children[0].checked == true ) {
            oldrow = tableObj.rows[i];
        }
    }

    oldrow.cells[0].children[0].checked=false;
    row.cells[0].children[0].checked=true;
    row.className = "activerow";
}


// 预备切换表格行的样式
function activeliqlistRow ( row ) {
    if ( liqlistdisabled == "true" ) return ;
    activeRadioTableRow ( document.all.liqList, row );
}

// 获得选中的行
function radio_active(radio_group) {
    for(counter =0;counter<radio_group.length;counter++) {
        if(radio_group[counter].checked) {
            return counter;
        }
    }
    return -1;
}