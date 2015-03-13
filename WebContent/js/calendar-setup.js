
var oldLink = null;
// code to change the active stylesheet
function setActiveStyleSheet(link, title) {
  var i, a, main;
  for(i=0; (a = document.getElementsByTagName("link")[i]); i++) {
    if(a.getAttribute("rel").indexOf("style") != -1 && a.getAttribute("title")) {
      a.disabled = true;
      if(a.getAttribute("title") == title) a.disabled = false;
    }
  }
  if (oldLink) oldLink.style.fontWeight = 'normal';
  oldLink = link;
  link.style.fontWeight = 'bold';
  return false;
}

// This function gets called when the end-user clicks on some date.
function selected(cal, date) {
  cal.sel.value = date; // just update the date in the input field.
  if (cal.dateClicked && (cal.sel.id == "sel1" || cal.sel.id == "sel3"))
    // if we add this call we close the calendar on single-click.
    // just to exemplify both cases, we are using this only for the 1st
    // and the 3rd field, while 2nd and 4th will still require double-click.
    cal.callCloseHandler();
}

// And this gets called when the end-user clicks on the _selected_ date,
// or clicks on the "Close" button.  It just hides the calendar without
// destroying it.
function closeHandler(cal) {
  cal.hide();                      // hide the calendar
    if(document==window.parent.document.frames.data_eara_frame_up.document) {
        window.parent.document.getElementById("data_eara_frame_up").height=window.parent.document.getElementById("data_eara_frame_up").height-180;
    }
  //var heightnew;
  //heightnew=parent.document.getElementById("data_eara_frame_up").style.height;
  //parent.document.getElementById("data_eara_frame_up").style.height=heightnew-300;
  //parent.document.getElementById("data_eara_frame_down").style.display="block";
}

// This function shows the calendar under the element having the given id.
// It takes care of catching "mousedown" signals on document and hiding the
// calendar if the click was outside.



function showCalendar() {
    var id =null;
    var format = null;
    var form = null;
 if(arguments.length ==2){
    id = arguments[0];
     format = arguments[1];
 }else if(arguments.length==3){
    id = arguments[0];
     format = arguments[1];
     form = arguments[2];
 }else{
     alert("argument error in showCalendar()");
     return;
 }
 var el = form==null?document.getElementById(id):getElement(form,id);
    if(document==window.parent.document.frames.data_eara_frame_up.document) {
    	 var iframHeight=window.parent.document.getElementById("data_eara_frame_up").height;
          if(iframHeight-el.offsetHeight<300){
        	 window.parent.document.getElementById("data_eara_frame_up").height=iframHeight-(-180); 
          }
    }
  
//alert(window.parent.document.getElementById("data_eara_frame_up").height);
//parent.document.getElementById("data_eara_frame_down").style.display="none";
 
  if (calendar != null) {

    // we already have some calendar created
    calendar.hide();                 // so we hide it first.
  } else {
	  
    // first-time call, create the calendar.
    var cal = new Calendar(false, null, selected, closeHandler);
    // uncomment the following line to hide the week numbers
    // cal.weekNumbers = false;
    calendar = cal;                  // remember it in the global var
    cal.setRange(1900, 2070);        // min/max year allowed.
    cal.create();
  }
  calendar.setDateFormat(format);    // set the specified date format
  calendar.parseDate(el.value);      // try to parse the text in field
  calendar.sel = el;                 // inform it what input field we use
  // the reference element that we pass to showAtElement is the button that
  // triggers the calendar.  In this example we align the calendar bottom-right
  // to the button.
  calendar.showAtElement(el.nextSibling, "Br");        // show the calendar

  return false;
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

var MINUTE = 60 * 1000;
var HOUR = 60 * MINUTE;
var DAY = 24 * HOUR;
var WEEK = 7 * DAY;

// If this handler returns true then the "date" given as
// parameter will be disabled.  In this example we enable
// only days within a range of 10 days from the current
// date.
// You can use the functions date.getFullYear() -- returns the year
// as 4 digit number, date.getMonth() -- returns the month as 0..11,
// and date.getDate() -- returns the date of the month as 1..31, to
// make heavy calculations here.  However, beware that this function
// should be very fast, as it is called for each day in a month when
// the calendar is (re)constructed.
function isDisabled(date) {
  var today = new Date();
  return (Math.abs(date.getTime() - today.getTime()) / DAY) > 10;
}

function flatSelected(cal, date) {
  var el = document.getElementById("preview");
  el.innerHTML = date;
}

function showFlatCalendar() {
  var parent = document.getElementById("display");

  // construct a calendar giving only the "selected" handler.
  var cal = new Calendar(false, null, flatSelected);

  // hide week numbers
  cal.weekNumbers = false;

  // We want some dates to be disabled; see function isDisabled above
  cal.setDisabledHandler(isDisabled);
  cal.setDateFormat("DD, M d");

  // this call must be the last as it might use data initialized above; if
  // we specify a parent, as opposite to the "showCalendar" function above,
  // then we create a flat calendar -- not popup.  Hidden, though, but...
  cal.create(parent);

  // ... we can show it here.
  cal.show();
}

