package ylr.actions.background.sys.menu;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 菜单数据查询。
 * 
 * @author 董帅 创建时间：2013-07-15 14:16:17
 *
 */
public class MenuQueryAction extends ActionSupport
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 父菜单id，-1表示顶级菜单。
	 */
	private int parentId = -1;
	
	/**
	 * 菜单id，-1表示未菜单，编辑新增数据。
	 */
	private int menuId = -1;
	
	/**
	 * 菜单名称。
	 */
	private String menuName = "";
	
	/**
	 * 菜单url地址。
	 */
	private String menuURL = "";
	
	/**
	 * 菜单图标。
	 */
	private String menuIcon = "";
	
	/**
	 * 桌面图标。
	 */
	private String menuDesktopIcon = "";
	
	/**
	 * 菜单序号。
	 */
	private int menuOrder = 0;
	
	public String execute()
	{
		if(this.menuId != -1)
		{
			//查询菜单数据。
			
		}
		return SUCCESS;
	}
	
	public int getParentId()
	{
		return parentId;
	}

	public void setParentId(int parentId)
	{
		this.parentId = parentId;
	}

	public int getMenuId()
	{
		return menuId;
	}

	public void setMenuId(int menuId)
	{
		this.menuId = menuId;
	}

	public String getMenuName()
	{
		return menuName;
	}

	public void setMenuName(String menuName)
	{
		this.menuName = menuName;
	}

	public String getMenuURL()
	{
		return menuURL;
	}

	public void setMenuURL(String menuURL)
	{
		this.menuURL = menuURL;
	}

	public String getMenuIcon()
	{
		return menuIcon;
	}

	public void setMenuIcon(String menuIcon)
	{
		this.menuIcon = menuIcon;
	}

	public String getMenuDesktopIcon()
	{
		return menuDesktopIcon;
	}

	public void setMenuDesktopIcon(String menuDesktopIcon)
	{
		this.menuDesktopIcon = menuDesktopIcon;
	}

	public int getMenuOrder()
	{
		return menuOrder;
	}

	public void setMenuOrder(int menuOrder)
	{
		this.menuOrder = menuOrder;
	}

}
