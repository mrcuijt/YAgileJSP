package ylr.actions.background.sys.role;

import ylr.actions.background.sys.SystemConfig;
import ylr.database.system.role.RoleDataBase;
import ylr.database.system.role.RoleInfo;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 查询角色信息。
 * @author 董帅 创建时间：2013-07-29 14:27:44
 *
 */
public class RoleQueryAction extends ActionSupport
{
	private static final long serialVersionUID = 1L;
	
	private int roleId = -1;
	
	private String message = "";
	
	private RoleInfo role = null;

	public String execute()
	{
		try
		{
			if(this.roleId != -1)
			{
				//修改
				RoleDataBase db = RoleDataBase.createRoleDataBase(SystemConfig.databaseConfigFileName, SystemConfig.databaseConfigNodeName);
				if(null != db)
				{
					this.role = db.getRole(this.roleId);
					
					if(null == this.role)
					{
						Exception e = new Exception("获取数据失败！" + db.getLastErrorMessage());
						throw e;
					}
				}
				else
				{
					Exception e = new Exception("创建数据库访问对象失败！");
					throw e;
				}
			}
		}
		catch(Exception ex)
		{
			this.setMessage(ex.getMessage());
		}
		
		return NONE;
	}

	public int getRoleId()
	{
		return roleId;
	}

	public void setRoleId(int roleId)
	{
		this.roleId = roleId;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public RoleInfo getRole()
	{
		return role;
	}

	public void setRole(RoleInfo role)
	{
		this.role = role;
	}
}
