const BASE_URL =
    "http://localhost:8080/test5/student";

window.onload = function () {

    loadStudents();

};

function loadStudents() {

    fetch(BASE_URL + "?action=findAll")

        .then(response => response.json())

        .then(result => {

            console.log(result);

            let html = "";

            result.data.forEach(student => {

                html += `
                    <tr>

                        <td>
                            <input type="checkbox">
                        </td>

                        <td>${student.id}</td>

                        <td>${student.name}</td>

                        <td>${student.gender}</td>

                        <td>${student.age}</td>

                        <td>${student.phone}</td>

                        <td>${student.classId}</td>

                        <td>${student.college}</td>

                        <td>${student.major}</td>

                        <td>

                            <a href="#" class="operate-link">
                                查看
                            </a>

                            <a href="#" class="operate-link">
                                编辑
                            </a>

                            <a href="#" class="operate-link">
                                删除
                            </a>

                        </td>

                    </tr>
                `;
            });

            document.getElementById("studentBody").innerHTML =
                html;

        })

        .catch(error => {

            console.error(error);

            alert("获取学生数据失败");

        });
}