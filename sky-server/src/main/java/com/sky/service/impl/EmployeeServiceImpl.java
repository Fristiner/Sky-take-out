package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 对前端传过来的密码进行md5处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    // 新增员工方法
    @Override
    public void save(EmployeeDTO employeeDTO) {
        // 第一个方法
        Employee employee = new Employee();
//        employee.setName(employeeDTO.getName());
        // 对象属性拷贝

        BeanUtils.copyProperties(employeeDTO, employee);
        // 设置账号状态
        // 这里使用常量类
        employee.setStatus(StatusConstant.ENABLE);
        // 设置账号密码 默认密码
        String password = DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes());
        employee.setPassword(password);
        // 创建时间  设置当前记录的
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        // 设置当前记录创建人id 和修改人id
        // 根据token进行解析
        Long empID = BaseContext.getCurrentId();
        employee.setCreateUser(empID);
        employee.setUpdateUser(empID);
        employeeMapper.insert(employee);

    }

    /**
     * 分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // select * from employee limit 0,10
        // 开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        long total = page.getTotal();
        List<Employee> records = page.getResult();

        PageResult pageResult = new PageResult(total, records);

        return pageResult;
    }

    /**
     * 启用禁用员工账号
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        // update employee set status = ？ where id= ？
        Employee employee = new Employee();
        employee.setStatus(status);
        employee.setId(id);
//
//        Employee.builder()
//                .status(status)


        employeeMapper.update(employee);
    }

    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        employee.setPassword("****");

        return employee;
    }

    /**
     * 编辑员工信息
     *
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.update(employee);
    }

    @Override
    public Integer editPassword(PasswordEditDTO passwordEditDTO) {
        // 1.根据id获取密码
        String OldPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes());

        Long currentId = BaseContext.getCurrentId();
        Employee employee = employeeMapper.getById(currentId);
        // 为true
        if (!employee.getPassword().equals(OldPassword)) {
            // 密码校验错误
            // 返回 0
            return StatusConstant.DISABLE;
        }
        // 2.校验两次密码是否一致 | 前端已经完成无需校验
        // 设置新的密码到其中
        String newPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes());
        employee.setPassword(newPassword);
        employeeMapper.update(employee);
        return StatusConstant.ENABLE;
    }

}
