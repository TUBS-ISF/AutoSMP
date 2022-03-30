let z1 = 2;
function myFunction(id) {

	  var x = document.createElement("INPUT");
		x.setAttribute("type", "file");
		x.id = z1;
		linebreak = document.createElement("br");
		lbreak = document.createElement("hr");

	var div = document.createElement('div');
	div.innerHTML = '<div><p><hr>Table ' + z1+ ' : <input type="file" id="fileUpload" accept =".csv"/><button style ="color:red"; onclick="Delet(this)"><b>X</b></button><input type="button" id="upload" value="Upload" onclick="Upload()"/><div id="t"></div></p></div>';
	var view = document.createElement('view')
	view.innerHTML = '<input type="radio" id="tabtableview" name="viewtabs" checked="checked"><label for="tabtableview">Table '+ z1+ ' view</label><div class="tab"><div id="dvCSV"></div></div>';
	view.classList.add('mytabs');
	view.id = z1;
	switch(id)
{
	case "algorithm": 
	document.getElementById("alg").appendChild(lbreak);
	document .getElementById("alg").appendChild(x);
	document .getElementById("alg").appendChild(linebreak);
	break;
	case "benchmark": document.getElementById("ben").appendChild(lbreak);
	document .getElementById("ben").appendChild(x);
	document .getElementById("ben").appendChild(linebreak);
	break;
	case "request": document.getElementById("req").appendChild(lbreak);
	document .getElementById("req").appendChild(x);
	document .getElementById("req").appendChild(linebreak);
	break;
	case "tables": 
	document.getElementById("t").append(div);
	document.getElementById("tabs").append(view);

	z1 ++;
	break;
	case "criteria": document.getElementById("cr").appendChild(lbreak);
	document .getElementById("cr").appendChild(x);
	document .getElementById("cr").appendChild(linebreak);
	break;
}
}

	function setDefaultConfig(){
	document.getElementById("calcStab").checked = true;
	document.getElementById("verbLog").checked = true;
	document.getElementById("storeSam").checked = true;
	document.getElementById("sysIter").value = 1;
	document.getElementById("algIter").value = 1;
	document.getElementById("timeout").value = 86400000;
	document.getElementById("coverage").value = 2;
	document.getElementById("maxAll").value = "Xmx4g";
	document.getElementById("minAll").value = "Xmx2g";
	document.getElementById("randomSeed").value = 100;
	document.getElementById("maxSize").value = 1000;
		
	}
	
	function WriteToConfigFile() {
       	reader = new FileReader();
  		reader.addEventListener('load', (event) => {
    	alert(event.target.result);
		
		});
		
		var fileToLoad = new File([""],"C:/Users/Farah Sh/Downloads/Job/ISF/Frontend/exampleConfig.properties");
		
		reader.readAsDataURL(fileToLoad);
        

	}
 
	function Delet(id){
	id.parentNode.parentNode.remove();

	}
	function Upload() {
        var fileUpload = document.getElementById("fileUpload");
        var regex = /^([a-zA-Z0-9\s_\\.\-:])+(.csv|.txt)$/;
        if (regex.test(fileUpload.value.toLowerCase())) {
            if (typeof (FileReader) != "undefined") {
                var reader = new FileReader();
                reader.onload = function (e) {
                    var table = document.createElement("table");
					 table.border = '1';
					 table.style="background-color:#ccc";
                    var rows = e.target.result.split("\n");
                    for (var i = 0; i < rows.length; i++) {
                        var cells = rows[i].split(";");
                        if (cells.length > 1) {
                            var row = table.insertRow(-1);
                            for (var j = 0; j < cells.length; j++) {
                                var cell = row.insertCell(-1);
                                cell.innerHTML = cells[j];
                            }
                        }
                    }
                    var dvCSV = document.getElementById("dvCSV");
                    dvCSV.innerHTML = "";
                    dvCSV.appendChild(table);
		    document.getElementById("upload").style.backgroundColor='green';	
                }
                reader.readAsText(fileUpload.files[0]);
            } else {
                alert("This browser does not support HTML5.");
            }
        } else {
            alert("Please upload a valid CSV file.");
        }
    }