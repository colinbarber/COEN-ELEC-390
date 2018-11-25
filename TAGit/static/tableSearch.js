function tableSearch() {
  var input, filter, table, tr, td, i, j, bool;
  input = document.getElementById("myInput");
  filter = input.value.toUpperCase();
  table = document.getElementById("sortTable");
  tr = table.getElementsByTagName("tr");
  for (i = 1; i < tr.length; i++) {
  bool=true;
  	td2=tr[i].getElementsByTagName("td");
    for(j=0; j<td2.length; j++){
      td = td2[j];
      if (td) {
        if (td.innerHTML.toUpperCase().indexOf(filter) > -1) {
          bool = true;
          break;
        } else {
          bool = false;
        }
      }
    }
    if (bool) {
          tr[i].style.display = "";
        } else {
          tr[i].style.display = "none";
        }
  }
}