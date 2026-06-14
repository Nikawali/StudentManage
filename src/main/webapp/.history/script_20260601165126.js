const API_BASE = 'http://localhost:8080/student';   // 根据你的部署路径修改

const StudentSystem = {
    pageConfig: {
        pageSize: 10,
        currentPage: 1,
        totalCount: 0,
        totalPage: 1
    },

    async apiRequest(url, method = 'GET', body = null) {
        const options = { method, headers: { 'Content-Type': 'application/x-www-form-urlencoded' } };
        if (body) options.body = new URLSearchParams(body);

        try {
            const res = await fetch(url, options);
            if (!res.ok) throw new Error('请求失败');
            return await res.json();
        } catch (err) {
            console.error(err);
            alert('请求失败：' + err.message);
            return null;
        }
    },

    async fetchStudents() {
        const result = await this.apiRequest(`${API_BASE}?action=findAll`);
        return result && result.code === 200 ? result.data : [];
    },

    async fetchStudentById(id) {
        const result = await this.apiRequest(`${API_BASE}?action=findById&id=${id}`);
        return result && result.code === 200 ? result.data : null;
    },

    async renderTable() {
        const tbody = document.getElementById('student-tbody');
        tbody.innerHTML = '<tr><td colspan="10">加载中...</td></tr>';

        const students = await this.fetchStudents();

        this.pageConfig.totalCount = students.length;
        this.pageConfig.totalPage = Math.ceil(students.length / this.pageConfig.pageSize);

        tbody.innerHTML = '';
        if (students.length === 0) {
            tbody.innerHTML = '<tr><td colspan="10">暂无数据</td></tr>';
            this.updatePageInfo();
            return;
        }

        const startIndex = (this.pageConfig.currentPage - 1) * this.pageConfig.pageSize;
        const currentData = students.slice(startIndex, startIndex + this.pageConfig.pageSize);

        currentData.forEach((student, idx) => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><input type="checkbox" class="stu-check" data-id="${student.id}"></td>
                <td>${startIndex + idx + 1}</td>
                <td>${student.id}</td>
                <td>${student.name || ''}</td>
                <td>${student.college || '未设置'}</td>
                <td>${student.major || '未设置'}</td>
                <td>-</td>
                <td>${student.classId || '未分配'}</td>
                <td>${student.age || ''}</td>
                <td>
                    <a href="javascript:;" class="operate-link view-link" data-id="${student.id}">查看</a>
                    <a href="javascript:;" class="operate-link edit-link" data-id="${student.id}">编辑</a>
                </td>
            `;
            tbody.appendChild(tr);
        });

        this.bindTableEvents();
        this.updatePageInfo();
    },

    bindTableEvents() {
        const tbody = document.getElementById('student-tbody');
        tbody.onclick = async (e) => {
            const link = e.target.closest('a');
            if (!link) return;

            const id = link.dataset.id;
            if (link.classList.contains('view-link')) {
                const student = await this.fetchStudentById(id);
                if (student) this.openModal('view', student);
            } else if (link.classList.contains('edit-link')) {
                const student = await this.fetchStudentById(id);
                if (student) this.openModal('edit', student);
            }
        };
    },

    openModal(type, student = null) {
        const modal = document.getElementById('operate-modal');
        const title = document.getElementById('modal-title');
        const saveBtn = document.getElementById('save-btn');

        document.getElementById('operate-type').value = type;

        if (type === 'view' && student) {
            title.textContent = '查看学生信息';
            this.fillForm(student);
            document.querySelectorAll('.form-input').forEach(el => el.disabled = true);
            saveBtn.style.display = 'none';
        } else if (type === 'edit' && student) {
            title.textContent = '编辑学生信息';
            this.fillForm(student);
            document.querySelectorAll('.form-input').forEach(el => el.disabled = false);
            saveBtn.style.display = 'inline-block';
            document.getElementById('current-id').value = student.id;
        } else {
            title.textContent = '新增学生信息';
            document.getElementById('operate-form').reset();
            document.querySelectorAll('.form-input').forEach(el => el.disabled = false);
            saveBtn.style.display = 'inline-block';
            document.getElementById('current-id').value = '';
        }

        modal.classList.add('active');
    },

    fillForm(student) {
        document.getElementById('stu-name').value = student.name || '';
        document.getElementById('stu-gender').value = student.gender || '男';
        document.getElementById('stu-age').value = student.age || '';
        document.getElementById('stu-phone').value = student.phone || '';
        document.getElementById('stu-class').value = student.classId || '';
    },

    async saveStudent() {
        const type = document.getElementById('operate-type').value;
        const id = document.getElementById('current-id').value;

        const data = {
            name: document.getElementById('stu-name').value,
            gender: document.getElementById('stu-gender').value,
            age: document.getElementById('stu-age').value,
            phone: document.getElementById('stu-phone').value,
            classId: document.getElementById('stu-class').value
        };

        let result;
        if (type === 'edit' && id) {
            data.id = id;
            result = await this.apiRequest(`${API_BASE}?action=update`, 'POST', data);
        } else {
            result = await this.apiRequest(`${API_BASE}?action=add`, 'POST', data);
        }

        if (result && result.code === 200) {
            alert(result.message || '操作成功');
            this.closeModal();
            this.renderTable();
        } else {
            alert(result?.message || '保存失败');
        }
    },

    closeModal() {
        document.getElementById('operate-modal').classList.remove('active');
    },

    updatePageInfo() {
        const { currentPage, totalCount, pageSize } = this.pageConfig;
        document.getElementById('page-info').textContent = 
            `第${currentPage}页，共${totalCount}条，(每页显示${pageSize}条)`;
        
        document.getElementById('prev-page').disabled = currentPage === 1;
        document.getElementById('next-page').disabled = currentPage === this.pageConfig.totalPage;
    },

    changePage(direction) {
        if (direction === 'prev' && this.pageConfig.currentPage > 1) this.pageConfig.currentPage--;
        if (direction === 'next' && this.pageConfig.currentPage < this.pageConfig.totalPage) this.pageConfig.currentPage++;
        this.renderTable();
    },

    bindEvents() {
        document.getElementById('add-btn').addEventListener('click', () => this.openModal('add'));
        document.getElementById('save-btn').addEventListener('click', () => this.saveStudent());
        document.getElementById('close-modal').addEventListener('click', () => this.closeModal());
        document.getElementById('cancel-btn').addEventListener('click', () => this.closeModal());
        document.getElementById('prev-page').addEventListener('click', () => this.changePage('prev'));
        document.getElementById('next-page').addEventListener('click', () => this.changePage('next'));
        document.getElementById('select-all').addEventListener('change', () => {
            document.querySelectorAll('.stu-check').forEach(cb => cb.checked = this.checked);
        });
    },

    init() {
        this.bindEvents();
        this.renderTable();
    }
};

document.addEventListener('DOMContentLoaded', () => StudentSystem.init());