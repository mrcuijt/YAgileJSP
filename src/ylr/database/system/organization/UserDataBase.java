package ylr.database.system.organization;

import java.util.ArrayList;
import java.util.List;

import ylr.YDB.YDataBase;
import ylr.YDB.YDataBaseConfig;
import ylr.YDB.YDataBaseType;
import ylr.YDB.YDataTable;
import ylr.YDB.YSqlParameters;

/**
 * 用户数据库操作类。。
 * @author 董帅 创建时间：2013-07-03 14:14:38
 *
 */
public class UserDataBase
{
	/**
	 * 用户数据库。
	 */
	private YDataBase userDatabase;
	
	/**
	 * 最后一次错误信息。
	 */
	private String lastErrorMessage = "";

	public YDataBase getUserDatabase()
	{
		return userDatabase;
	}

	public void setUserDatabase(YDataBase userDatabase)
	{
		this.userDatabase = userDatabase;
	}
	
	/**
	 * 获取最后一次错误信息。
	 * 
	 * @return 错误信息。
	 */
	public String getLastErrorMessage()
	{
		return this.lastErrorMessage;
	}
	
	/**
	 * 创建用户数据库操作对象。
	 * 
	 * @param configFile 数据库配置文件路径。
	 * @param nodeName 数据库配置节点名称。
	 * @return 用户数据库操作对象。
	 * @throws Exception 未处理异常。
	 */
	static public UserDataBase createUserDataBase(String configFile,String nodeName) throws Exception
	{
		UserDataBase db = new UserDataBase();
		
		try
		{
			db.setUserDatabase(YDataBaseConfig.getDataBase(configFile, nodeName));
		}
		catch(Exception ex)
		{
			throw ex;
		}
		
		return db;
	}
	
	/**
	 * 用户登陆。
	 * 
	 * @param userName 用户名。
	 * @param userPassword 密码。
	 * @return 成功返回用户信息，否则返回null。
	 */
	public UserInfo login(String logName,String logPassword)
	{
		UserInfo user = null;
		
		try
		{
			if(this.userDatabase.connectDataBase())
			{
				user = this.login(logName, logPassword, this.userDatabase);
			}
			else
			{
				Exception e = new Exception("数据库连接失败！" + this.userDatabase.getLastErrorMessage());
				throw e;
			}
		}
		catch(Exception ex)
		{
			this.lastErrorMessage = ex.getMessage();
		}
		finally
		{
			this.userDatabase.disconnectDataBase();
		}
		
		return user;
	}
	
	/**
	 * 用户登陆。
	 * 
	 * @param userName 用户名。
	 * @param userPassword 密码。
	 * @param db 使用的数据库连接。
	 * @return 成功返回用户信息，否则返回null。
	 */
	public UserInfo login(String logName,String logPassword,YDataBase db)
	{
		UserInfo user = null;
		
		try
		{
			//构建SQL语句
			String sql = "";
			if(YDataBaseType.MSSQL == db.getDatabaseType())
			{
				sql = "SELECT TOP(1) * FROM ORG_USER WHERE ISDELETE = 'N' AND LOGNAME = ? AND LOGPASSWORD = ?";
			}
			else
			{
				Exception e = new Exception("不支持的数据库类型！");
				throw e;
			}
			
			//参数
			YSqlParameters ps = new YSqlParameters();
			ps.addParameter(1, logName);
			ps.addParameter(2, logPassword);
			
			//执行语句
			YDataTable table = db.executeSqlReturnData(sql, ps);
			if(null != table)
			{
				if(table.rowCount() > 0)
				{
					user = new UserInfo();
					if(null != table.getData(0,"ID"))
					{
						user.setId((Integer)table.getData(0,"ID"));
					}
					
					if(null != table.getData(0,"NAME"))
					{
						user.setName((String)table.getData(0,"NAME"));
					}
					
					if(null != table.getData(0,"LOGNAME"))
					{
						user.setLogName((String)table.getData(0,"LOGNAME"));
					}
					
					if(null != table.getData(0, "LOGPASSWORD"))
					{
						user.setLogPassword((String)table.getData(0, "LOGPASSWORD"));
					}
					
					if(null != table.getData(0, "ORGANIZATIONID"))
					{
						user.setOrganizationId((Integer)table.getData(0, "ORGANIZATIONID"));
					}
					
					if(null != table.getData(0, "ISDELETE"))
					{
						String isDelete = (String)table.getData(0, "ISDELETE");
						if(isDelete.equals("N"))
						{
							user.setDelete(false);
						}
						else
						{
							user.setDelete(true);
						}
					}
					
					if(null != table.getData(0, "ORDER"))
					{
						user.setOrder((Integer)table.getData(0, "ORDER"));
					}
				}
				else
				{
					Exception e = new Exception("用户名或密码错误！");
					throw e;
				}
			}
			else
			{
				Exception e = new Exception(db.getLastErrorMessage());
				throw e;
			}
		}
		catch(Exception ex)
		{
			this.lastErrorMessage = ex.getMessage();
		}
		
		return user;
	}
	
	/**
	 * 修改用户密码。
	 * 
	 * @param id 用户id。
	 * @param oldPsw 旧密码。
	 * @param newPsw 新密码。
	 * 
	 * @return 成功返回true，否则返回false。
	 */
	public boolean changePassword(int id,String oldPsw,String newPsw)
	{
		boolean retValue = false;
		
		try
		{
			if(this.userDatabase.connectDataBase())
			{
				retValue = this.changePassword(id, oldPsw, newPsw, this.userDatabase);
			}
			else
			{
				Exception e = new Exception("数据库连接失败！" + this.userDatabase.getLastErrorMessage());
				throw e;
			}
		}
		catch(Exception ex)
		{
			this.lastErrorMessage = ex.getMessage();
		}
		finally
		{
			this.userDatabase.disconnectDataBase();
		}
		
		return retValue;
	}
	
	/**
	 * 修改用户密码。
	 * 
	 * @param id 用户id。
	 * @param oldPsw 旧密码。
	 * @param newPsw 新密码。
	 * @param db 使用的数据库连接。
	 * 
	 * @return 成功返回true，否则返回false。
	 */
	public boolean changePassword(int id,String oldPsw,String newPsw,YDataBase db)
	{
		boolean retValue = false;
		
		try
		{
			//构建SQL语句
			String sql = "";
			if(YDataBaseType.MSSQL == db.getDatabaseType())
			{
				sql = "UPDATE ORG_USER SET LOGPASSWORD = ? WHERE ID = ? AND LOGPASSWORD = ?";
			}
			else
			{
				Exception e = new Exception("不支持的数据库类型！");
				throw e;
			}
			
			//参数
			YSqlParameters ps = new YSqlParameters();
			ps.addParameter(1, newPsw);
			ps.addParameter(2, id);
			ps.addParameter(3, oldPsw);
			
			//执行语句
			int rowCount = db.executeSqlWithOutData(sql, ps);
			if(rowCount > 0)
			{
				retValue = true;
			}
			else if(rowCount == 0)
			{
				Exception e = new Exception("原密码输入错误。");
				throw e;
			}
			else
			{
				Exception e = new Exception(db.getLastErrorMessage());
				throw e;
			}
		}
		catch(Exception ex)
		{
			this.lastErrorMessage = ex.getMessage();
		}
		
		return retValue;
	}
	
	/**
	 * 修改用户密码。
	 * 
	 * @param id 用户id。
	 * @param newPsw 新密码。
	 * 
	 * @return 成功返回true，否则返回false。
	 */
	public boolean changePassword(int id,String newPsw)
	{
		boolean retValue = false;
		
		try
		{
			if(this.userDatabase.connectDataBase())
			{
				retValue = this.changePassword(id, newPsw, this.userDatabase);
			}
			else
			{
				Exception e = new Exception("数据库连接失败！" + this.userDatabase.getLastErrorMessage());
				throw e;
			}
		}
		catch(Exception ex)
		{
			this.lastErrorMessage = ex.getMessage();
		}
		finally
		{
			this.userDatabase.disconnectDataBase();
		}
		
		return retValue;
	}
	
	/**
	 * 修改用户密码。
	 * 
	 * @param id 用户id。
	 * @param newPsw 新密码。
	 * @param db 使用的数据库连接。
	 * 
	 * @return 成功返回true，否则返回false。
	 */
	public boolean changePassword(int id,String newPsw,YDataBase db)
	{
		boolean retValue = false;
		
		try
		{
			//构建SQL语句
			String sql = "";
			if(YDataBaseType.MSSQL == db.getDatabaseType())
			{
				sql = "UPDATE ORG_USER SET LOGPASSWORD = ? WHERE ID = ?";
			}
			else
			{
				Exception e = new Exception("不支持的数据库类型！");
				throw e;
			}
			
			//参数
			YSqlParameters ps = new YSqlParameters();
			ps.addParameter(1, newPsw);
			ps.addParameter(2, id);
			
			//执行语句
			int rowCount = db.executeSqlWithOutData(sql, ps);
			if(rowCount > 0)
			{
				retValue = true;
			}
			else if(rowCount == 0)
			{
				Exception e = new Exception("修改密码失败。");
				throw e;
			}
			else
			{
				Exception e = new Exception(db.getLastErrorMessage());
				throw e;
			}
		}
		catch(Exception ex)
		{
			this.lastErrorMessage = ex.getMessage();
		}
		
		return retValue;
	}
	
	/**
	 * 创建用户。
	 * 
	 * @param user 用户信息。
	 * @return 成功返回用户id，否发返回-1。
	 */
	public int createUser(UserInfo user)
	{
		int retValue = -1;
		
		try
		{
			if(this.userDatabase.connectDataBase())
			{
				retValue = this.createUser(user, this.userDatabase);
			}
			else
			{
				Exception e = new Exception("连接数据库失败！" + this.userDatabase.getLastErrorMessage());
				throw e;
			}
		}
		catch(Exception ex)
		{
			this.lastErrorMessage = ex.getMessage();
		}
		finally
		{
			this.userDatabase.disconnectDataBase();
		}
		
		return retValue;
	}
	
	/**
	 * 创建用户。
	 * 
	 * @param user 用户信息。
	 * @param db 使用的数据库连接。
	 * @return 成功返回用户id，否发返回-1。
	 */
	public int createUser(UserInfo user,YDataBase db)
	{
		int retValue = -1;
		
		try
		{
			if(null == user)
			{
				Exception e = new Exception("用户信息为null！");
				throw e;
			}
			else if(null == user.getName() || user.getName().equals(""))
			{
				Exception e = new Exception("未设置用户姓名！");
				throw e;
			}
			else if(user.getName().length() > 20)
			{
				Exception e = new Exception("用户姓名字符个数超过20！");
				throw e;
			}
			else if(null == user.getLogName() || user.getLogName().equals(""))
			{
				Exception e = new Exception("未设置用户登陆名！");
				throw e;
			}
			else if(user.getLogName().length() > 20)
			{
				Exception e = new Exception("用户登陆名字符个数超过20！");
				throw e;
			}
			else if(user.getLogPassword() == null || user.getLogPassword().length() > 40)
			{
				Exception e = new Exception("用户登陆密码不合法，不能大于40个字符！");
				throw e;
			}
			else
			{
				//判断用户是否存在
				if(this.existUser(user.getLogName(), db))
				{
					Exception e = new Exception("用户登录名已存在！");
					throw e;
				}
				
				//构建SQL语句
				String sql = "";
				//参数
				YSqlParameters ps = new YSqlParameters();
				
				if(user.getOrganizationId() == -1)
				{
					//顶级机构用户
					if(YDataBaseType.MSSQL == db.getDatabaseType())
					{
						sql = "INSERT INTO ORG_USER (LOGNAME,LOGPASSWORD,NAME,[ORDER]) VALUES (?,?,?,?)";
						ps.addParameter(1, user.getLogName());
						ps.addParameter(2, user.getLogPassword());
						ps.addParameter(3, user.getName());
						ps.addParameter(4, user.getOrder());
					}
					else
					{
						Exception e = new Exception("不支持的数据库类型！");
						throw e;
					}
				}
				else
				{
					if(YDataBaseType.MSSQL == db.getDatabaseType())
					{
						sql = "INSERT INTO ORG_USER (LOGNAME,LOGPASSWORD,NAME,ORGANIZATIONID,[ORDER]) VALUES (?,?,?,?,?)";
						ps.addParameter(1, user.getLogName());
						ps.addParameter(2, user.getLogPassword());
						ps.addParameter(3, user.getName());
						ps.addParameter(4, user.getOrganizationId());
						ps.addParameter(5, user.getOrder());
					}
					else
					{
						Exception e = new Exception("不支持的数据库类型！");
						throw e;
					}
				}
				
				//执行语句。
				int rowCount = db.executeSqlWithOutData(sql, ps);
				if(rowCount > 0)
				{
					//获取id
					YDataTable table = db.executeSqlReturnData("SELECT @@IDENTITY AS ID");
					
					if(null != table && table.rowCount() > 0)
					{
						if(null != table.getData(0,"ID"))
						{
							double d = (Double)table.getData(0,"ID");
							retValue = (int) d;
						}
					}
					else
					{
						Exception e = new Exception(db.getLastErrorMessage());
						throw e;
					}
				}
				else
				{
					Exception e = new Exception(db.getLastErrorMessage());
					throw e;
				}
			}
		}
		catch(Exception ex)
		{
			this.lastErrorMessage = ex.getMessage();
		}
		
		return retValue;
	}
	
	/**
	 * 判断用户登陆名是否存在。
	 * 
	 * @param logName 登录名。
	 * @param db 使用的数据库连接。
	 * @return 存在返回true，否则返回false。
	 * @throws Exception 未处理的异常。
	 */
	public boolean existUser(String logName,YDataBase db) throws Exception
    {
		boolean retValue = false;
        try
        {
        	//构建SQL语句
        	String sql = "";
        	YSqlParameters ps = new YSqlParameters();
        	ps.addParameter(1, logName);
        	
        	if(YDataBaseType.MSSQL == db.getDatabaseType())
			{
        		
				sql = "SELECT 'true' WHERE EXISTS (SELECT * FROM ORG_USER WHERE LOGNAME = ?)";
			}
			else
			{
				Exception e = new Exception("不支持的数据库类型！");
				throw e;
			}
        	
            //执行
        	YDataTable table = db.executeSqlReturnData(sql, ps);
        	if(table == null)
        	{
        		Exception e = new Exception("获取数据出错！" + db.getLastErrorMessage());
				throw e;
        	}
        	else if(table.rowCount() > 0)
        	{
        		retValue = true;
        	}
        }
        catch (Exception ex)
        {
            throw ex;
        }

        return retValue;
    }
	
	/**
	 * 获取指定组织机构id的用户列表。
	 * @param orgId 组织机构id。
	 * @return 成功返回用户列表，否则返回null。
	 */
	public List<UserInfo> getUsersByOrganizationId(int orgId)
	{
		List<UserInfo> users = null;
		
		try
		{
			if(this.userDatabase.connectDataBase())
			{
				users = this.getUsersByOrganizationId(orgId, this.userDatabase);
			}
			else
			{
				Exception e = new Exception("连接数据库失败！" + this.userDatabase.getLastErrorMessage());
				throw e;
			}
		}
		catch(Exception ex)
		{
			this.lastErrorMessage = ex.getMessage();
		}
		finally
		{
			this.userDatabase.disconnectDataBase();
		}
		
		return users;
	}
	
	/**
	 * 获取指定组织机构id的用户列表。
	 * @param orgId 组织机构id。
	 * @param db 使用的数据库连接。
	 * @return 成功返回用户列表，否则返回null。
	 */
	public List<UserInfo> getUsersByOrganizationId(int orgId,YDataBase db)
	{
		List<UserInfo> users = null;
		
		try
		{
			//构建SQL语句
			String sql = "";
			
			if(orgId == -1)
			{
				//顶级机构
				if(YDataBaseType.MSSQL == db.getDatabaseType())
				{
					sql = "SELECT * FROM ORG_USER WHERE ISDELETE = 'N' AND ORGANIZATIONID IS NULL AND ID <> 1 ORDER BY [ORDER] ASC";
				}
				else
				{
					Exception e = new Exception("不支持的数据库类型！");
					throw e;
				}
			}
			else
			{
				//下级机构
				if(YDataBaseType.MSSQL == db.getDatabaseType())
				{
					sql = "SELECT * FROM ORG_USER WHERE ISDELETE = 'N' AND ORGANIZATIONID = ? AND ID <> 1 ORDER BY [ORDER] ASC";
				}
				else
				{
					Exception e = new Exception("不支持的数据库类型！");
					throw e;
				}
			}
			
			YDataTable table = null;
			if(orgId == -1)
			{
				//顶级机构
				table = db.executeSqlReturnData(sql);
			}
			else
			{
				//参数
				YSqlParameters ps = new YSqlParameters();
				ps.addParameter(1, orgId);
				
				table = db.executeSqlReturnData(sql,ps);
			}
			
			if(null != table)
			{
				users = new ArrayList<UserInfo>();
				
				for(int i = 0;i < table.rowCount();i++)
				{
					UserInfo u = new UserInfo();
					
					if(null != table.getData(i,"ID"))
					{
						u.setId((Integer)table.getData(i,"ID"));
					}
					
					if(null != table.getData(i,"NAME"))
					{
						u.setName((String)table.getData(i,"NAME"));
					}
					
					if(null != table.getData(i,"ORGANIZATIONID"))
					{
						u.setOrganizationId((Integer)table.getData(i,"ORGANIZATIONID"));
					}
					
					if(null != table.getData(i, "LOGNAME"))
					{
						u.setLogName((String)table.getData(i, "LOGNAME"));
					}
					
					if(null != table.getData(i, "LOGPASSWORD"))
					{
						u.setLogPassword((String)table.getData(i, "LOGPASSWORD"));
					}
					
					if(null != table.getData(i, "ISDELETE"))
					{
						String isDelete = (String)table.getData(i, "ISDELETE");
						if(isDelete.equals("N"))
						{
							u.setDelete(false);
						}
						else
						{
							u.setDelete(true);
						}
					}
					
					if(null != table.getData(i, "ORDER"))
					{
						u.setOrder((Integer)table.getData(i, "ORDER"));
					}
					
					users.add(u);
				}
			}
			else
			{
				Exception e = new Exception(db.getLastErrorMessage());
				throw e;
			}
		}
		catch(Exception ex)
		{
			this.lastErrorMessage = ex.getMessage();
		}
		
		return users;
	}
	
	/**
	 * 获取指定的用户。
	 * @param id 用户id。
	 * @return 成功返回用户信息，否则返回null。
	 */
	public UserInfo getUser(int id)
	{
		UserInfo user = null;
		
		try
		{
			if(this.userDatabase.connectDataBase())
			{
				user = this.getUser(id, this.userDatabase);
			}
			else
			{
				Exception e = new Exception("连接数据库失败！" + this.userDatabase.getLastErrorMessage());
				throw e;
			}
		}
		catch(Exception ex)
		{
			this.lastErrorMessage = ex.getMessage();
		}
		finally
		{
			this.userDatabase.disconnectDataBase();
		}
		
		return user;
	}
	
	/**
	 * 获取指定的用户。
	 * @param id 用户id。
	 * @param db 使用的数据库连接。
	 * @return 成功返回用户信息，否则返回null。
	 */
	public UserInfo getUser(int id,YDataBase db)
	{
		UserInfo user = null;
		
		try
		{
			//构建SQL语句
        	String sql = "";
        	YSqlParameters ps = new YSqlParameters();
        	ps.addParameter(1, id);
        	
        	if(YDataBaseType.MSSQL == db.getDatabaseType())
			{
        		
				sql = "SELECT TOP(1) * FROM ORG_USER WHERE ISDELETE = 'N' AND ID = ?";
			}
			else
			{
				Exception e = new Exception("不支持的数据库类型！");
				throw e;
			}
        	
            //执行
        	YDataTable table = db.executeSqlReturnData(sql, ps);
        	if(table != null)
        	{
        		if(table.rowCount() > 0)
        		{
        			user = new UserInfo();
        			
        			if(null != table.getData(0,"ID"))
					{
						user.setId((Integer)table.getData(0,"ID"));
					}
					
					if(null != table.getData(0,"NAME"))
					{
						user.setName((String)table.getData(0,"NAME"));
					}
					
					if(null != table.getData(0,"ORGANIZATIONID"))
					{
						user.setOrganizationId((Integer)table.getData(0,"ORGANIZATIONID"));
					}
					
					if(null != table.getData(0, "LOGNAME"))
					{
						user.setLogName((String)table.getData(0, "LOGNAME"));
					}
					
					if(null != table.getData(0, "LOGPASSWORD"))
					{
						user.setLogPassword((String)table.getData(0, "LOGPASSWORD"));
					}
					
					if(null != table.getData(0, "ISDELETE"))
					{
						String isDelete = (String)table.getData(0, "ISDELETE");
						if(isDelete.equals("N"))
						{
							user.setDelete(false);
						}
						else
						{
							user.setDelete(true);
						}
					}
					
					if(null != table.getData(0, "ORDER"))
					{
						user.setOrder((Integer)table.getData(0, "ORDER"));
					}
        		}
        		else
        		{
        			Exception e = new Exception("未找到指定用户！" + db.getLastErrorMessage());
    				throw e;
        		}
        	}
        	else
        	{
        		Exception e = new Exception("获取数据出错！" + db.getLastErrorMessage());
				throw e;
        	}
		}
		catch(Exception ex)
		{
			this.lastErrorMessage = ex.getMessage();
		}
		
		return user;
	}
	
	/**
	 * 修改用户信息（不改变用户密码和所属机构）。
	 * @param user 用户信息。
	 * @return 成功返回true，否则返回false。
	 */
	public boolean changeUser(UserInfo user)
	{
		boolean retValue = false;
		
		try
		{
			if(this.userDatabase.connectDataBase())
			{
				retValue = this.changeUser(user, this.userDatabase);
			}
			else
			{
				Exception e = new Exception("连接数据库失败！" + this.userDatabase.getLastErrorMessage());
				throw e;
			}
		}
		catch(Exception ex)
		{
			this.lastErrorMessage = ex.getMessage();
		}
		finally
		{
			this.userDatabase.disconnectDataBase();
		}
		
		return retValue;
	}
	
	/**
	 * 修改用户信息（不改变用户密码和所属机构）。
	 * @param user 用户信息。
	 * @param db 使用的数据库连接。
	 * @return 成功返回true，否则返回false。
	 */
	public boolean changeUser(UserInfo user,YDataBase db)
	{
		boolean retValue = false;
		
		try
		{
			//构建SQL语句
			String sql = "";
			if(YDataBaseType.MSSQL == db.getDatabaseType())
			{
				sql = "UPDATE ORG_USER SET LOGNAME = ?, NAME = ?,[ORDER] = ? WHERE ID = ?";
			}
			else
			{
				Exception e = new Exception("不支持的数据库类型！");
				throw e;
			}
			
			//参数
			YSqlParameters ps = new YSqlParameters();
			ps.addParameter(1, user.getLogName());
			ps.addParameter(2, user.getName());
			ps.addParameter(3, user.getOrder());
			ps.addParameter(4, user.getId());
			
			//执行语句
			int rowCount = db.executeSqlWithOutData(sql, ps);
			if(rowCount > 0)
			{
				retValue = true;
			}
			else if(rowCount == 0)
			{
				Exception e = new Exception("修改信息失败，未找到指定的用户。");
				throw e;
			}
			else
			{
				Exception e = new Exception(db.getLastErrorMessage());
				throw e;
			}
		}
		catch(Exception ex)
		{
			this.lastErrorMessage = ex.getMessage();
		}
		
		return retValue;
	}
}
