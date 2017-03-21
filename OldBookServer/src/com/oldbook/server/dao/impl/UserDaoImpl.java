package com.oldbook.server.dao.impl;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.mysql.jdbc.Blob;
import com.oldbook.api.OldbookApplication;
import com.oldbook.entity.BookEntity;
import com.oldbook.entity.BorrowEntity;
import com.oldbook.entity.MessageType;
import com.oldbook.entity.UserEntity;
import com.oldbook.server.dao.DButil;
import com.oldbook.server.dao.UserDao;
import com.oldbook.server.net.OutputThread;
import com.oldbook.server.util.MyDate;
import com.server.oldbook.Constants;

public class UserDaoImpl implements UserDao
{

	public String initial()
	{
		ResultSet result_avatar;
		ResultSet result_surface;
		Connection con = DButil.connect();
		String res="";
		
		try
		{
			
			String sql_avatar="select * from user_table";
			String sql_surface="select * from book_table";
			PreparedStatement psCount=con.prepareStatement(sql_avatar);
			result_avatar=psCount.executeQuery(sql_avatar);
			
			while(result_avatar.next())
			{
				try
				{
					int id=result_avatar.getInt("id");
					InputStream in=result_avatar.getBinaryStream("avatar");
					if(in!=null)
					{
						res+=String.valueOf(id+" "+in.available()+"_");
					}
					else
					{
						res+=String.valueOf(id+" "+0+"_");
					}
				}
				catch(IOException e)
				{
					
				}
			}
			if(res.equals(""))
				res="null";
			res+="@";
			
			psCount=con.prepareStatement(sql_surface);
			result_surface=psCount.executeQuery(sql_surface);
			while(result_surface.next())
			{
				try
				{
					int bookId=result_surface.getInt("bookId");
					InputStream in_b=result_surface.getBinaryStream("bookSurface");
					if(in_b!=null)
					{
						res+=String.valueOf(bookId+" "+in_b.available()+"_");
					}
					else
					{
						res+=String.valueOf(bookId+" "+0+"_");
					}
				}
				catch(IOException e)
				{
					
				}
			}
			
			return res;
			
		}
		catch(SQLException e)
		{
			return res;
		}
		
		
	}
	@Override
	public int register(UserEntity user)
	{
		int id;
		Connection con = DButil.connect();
		Blob x=null;

		try
		{
			//获取用户总数
			ResultSet resultCount;
			int count;
			String sqlCount="select count(*) from user_table";
			PreparedStatement psCount=con.prepareStatement(sqlCount);
			resultCount=psCount.executeQuery(sqlCount);
			resultCount.next();
			count=resultCount.getInt(1);
	        
			//查看是否已注册
			ResultSet resultIsIn;
		    String sqlIsIn="select * from user_table where Username=?";
			PreparedStatement psIsIn=con.prepareStatement(sqlIsIn);
			psIsIn.setString(1, user.getUsername());
			resultIsIn=psIsIn.executeQuery();
			resultIsIn.next();
			if(resultIsIn.getRow()==0)
			{
				//未注册就继续注册
				String sql = "insert into user_table (id,username,password,petname,avatar) values(?,?,?,?,?)";   //更改数据库
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, count+1);
				ps.setString(2, user.getUsername());
				ps.setString(3,user.getPassword());
				ps.setString(4, user.getPetname());
				ps.setBlob(5, x);
				//ps.setInt(4, user.getAvatar());
				//ps.setString(9, user.getTime());
				int r = ps.executeUpdate();
				
				String sql_evaluation="insert into personal_evaluation_table (id,evaluation) values(?,?)";
				PreparedStatement ps_evaluation=con.prepareStatement(sql_evaluation);
				ps_evaluation.setInt(1, count+1);
				ps_evaluation.setInt(2,100);
				r=ps_evaluation.executeUpdate();
				
				UserEntity u=new UserEntity();
				u.setId(count+1);
				u.setUsername(user.getUsername());
				u.setPetname(user.getPetname());
				OldbookApplication.online.put(count+1,u);
				if (r > 0)
				{
					return ++count;
				}
			}
			else
			{
				//已注册
				return -2;
			}
		}
		catch (SQLException e)
		{
			 e.printStackTrace();
		} 
		finally
		{
			DButil.close(con);
		}
		return -1;
	}

	/**
	 * 登录
	 * @param 登录用户
	 * @return 登录信息
	 */
	@Override
	public UserEntity login(UserEntity user)
	{
		//连接数据库
		Connection con = DButil.connect();
		//登录部分
		String sql = "select * from user_table where username=? and password=?";
		UserEntity u=new UserEntity();
		try
		{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPassword());
			ResultSet result = ps.executeQuery();
			result.last();
			int count=result.getRow();
			if(count!=0)
			{
				int id=Integer.parseInt(result.getObject(1).toString());
				String username=result.getObject(2).toString();
				String password=result.getObject(3).toString();
				String petname=result.getObject(4).toString();
				if(password.equals(user.getPassword()))
				{
					u.setId(id);
					u.setUsername(username);
					u.setPetname(petname);
					boolean isOnline=true;
					OldbookApplication.online.put(id,u);
					return u;
				}
				else
				{
					u.setId(-1);
					u.setUsername(null);
					u.setPassword(null);
					u.setPetname(null);
					return u;
				}
			}
			u.setId(-2);
			u.setUsername(null);
			u.setPassword(null);
			u.setPetname(null);
			return u;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DButil.close(con);
		}
		u.setId(-1);
		u.setUsername(null);
		u.setPassword(null);
		u.setPetname(null);
		return u;
	}

	
	/**
	 * 获取评价
	 * @param 用户id
	 * @return 评价
	 */
	public String getEvaluation(int id)
	{
		String res="";
		Connection con = DButil.connect();
		try
		{
			String sql = "select * from personal_evaluation_table where id=?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1,id);
			ResultSet rs = ps.executeQuery();
			rs.next();
			
			res+=rs.getString("evaluation");
			return res;
			
			
		}
		catch (SQLException e)
		{
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return null;
	}
	
	/**
	 * 获取一个人的个人信息
	 * @param 用户id
	 * @return 个人信息
	 */
	public String getOneInfor(int id)
	{
		String res="";
		Connection con = DButil.connect();
		try
		{

			UserEntity u=OldbookApplication.online.get(id);

			String sql = "select * from personal_evaluation_table where id=?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1,id);
			ResultSet rs = ps.executeQuery();
			rs.next();
			
			res+=String.valueOf(id)+"_"+u.getPetname()+"_"+rs.getString("evaluation")+" ";
			return res;
			
			
		}
		catch (SQLException e)
		{
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return null;
		
	}
	
	/**
	 * 添加新书
	 * @param 书信息
	 * @return bookId
	 */
	@Override
	public int addNewBook(BookEntity be)
	{
		
			int id;
			Connection con = DButil.connect();			
			try
			{
				//获取用户总数
				ResultSet resultCount;
				int count;
				Blob x=null;
				String sqlCount="select count(*) from book_table";
				PreparedStatement psCount=con.prepareStatement(sqlCount);
				resultCount=psCount.executeQuery(sqlCount);
				resultCount.next();
				count=resultCount.getInt(1);
				String sql = "insert into book_table (bookId,bookName,bookSurface,bookAuthor,bookPublishing,bookNumber,bookOwner) values(?,?,?,?,?,?,?)";   //更改数据库
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, count+1);
				ps.setString(2, be.getBookName());
				ps.setBlob(3, x);
				ps.setString(4, be.getBookAuthor());
				ps.setString(5,be.getBookPublishing());
				ps.setString(6, be.getbookNumber());
				ps.setInt(7, be.getOwner());
					//ps.setInt(4, user.getAvatar());
					//ps.setString(9, user.getTime());
					int r = ps.executeUpdate();
				
				if (r > 0)
				{
					return ++count;
				}
				
			}
			catch (SQLException e)
			{
				 e.printStackTrace();
			} 
			finally
			{
				DButil.close(con);
			}
			return -1;
	}
		
	/**
	 * 刷新好友列表
	 */
	public String refresh()
	{
		String res="";
		Connection con = DButil.connect();
		try
		{
			Set<Entry<Integer,UserEntity>> sets=OldbookApplication.online.entrySet();
			for (Entry<Integer,UserEntity> entry:sets)
			{
				UserEntity u=new UserEntity();
				Object key = entry.getKey();
				Object val = entry.getValue();
				u=(UserEntity)val;
			
				String sql = "select * from personal_evaluation_table where id=?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1,(int)key);
				ResultSet rs = ps.executeQuery();
				rs.next();
			
				res+=String.valueOf(u.getId())+"_"+u.getPetname()+"_"+rs.getString("evaluation")+" ";
			}
			if(res.equals(""))
				res="null";
			return res;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		} finally {
			DButil.close(con);
		}
		return "null";
	}

	public MessageType setSurface(String filePath,int bookId)
	{
		InputStream    in = null;
		Connection con = DButil.connect();
		try
		{
			in=new FileInputStream(filePath);
		
			String sql = "update book_table set bookSurface=? where bookId=?";   //更改数据库
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setBlob(1, in);
			ps.setInt(2,bookId);
			ps.executeUpdate();
            in.close();
            DButil.close(con);
		}
		catch(FileNotFoundException e)
		{
			System.out.println(e);
			return MessageType.FAIL;
		}
		catch(SQLException e)
		{
			System.out.println(e);
			return MessageType.FAIL;
		}
		catch(IOException e)
		{
			System.out.println(e);
			return MessageType.FAIL;
		}
		return MessageType.SUCCESS;
	}
	
	public InputStream getSurface(int bookId)
	{
		byte[] b=new byte[0];
		InputStream    in = null;
		try
		{
			Connection conNew = DButil.connect();
			ResultSet rs;
			String sql2="select bookSurface from book_table where bookId=?";
			PreparedStatement ps = conNew.prepareStatement(sql2);
			ps.setInt(1,bookId);
			rs=ps.executeQuery();
			rs.next();    //将光标指向第一行
			in=rs.getBinaryStream("bookSurface");
			/*
			b=new byte[in.available()];    //新建保存图片数据的byte数组
			in.read(b);
			OutputStream out=new FileOutputStream("D:/inspiration/OldBookServer/"+bookId+".jpg");
			out.write(b);
			out.flush();
			out.close();
			*/
			DButil.close(conNew);
			return in;
		}
		catch(SQLException e)
		{
			return in;
		}
		
			
	}

	public MessageType setAvatar(String filePath,int Id)
	{
		InputStream in = null;
		Connection con = DButil.connect();
		try
		{
			in=new FileInputStream(filePath);
		
			String sql = "update user_table set avatar=? where id=?";   //更改数据库
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setBlob(1, in);
			ps.setInt(2,Id);
			ps.executeUpdate();
            in.close();
            DButil.close(con);
            System.out.println("头像插入成功");
		}
		catch(FileNotFoundException e)
		{
			return MessageType.FAIL;
		}
		catch(SQLException e)
		{
			return MessageType.FAIL;
		}
		catch(IOException e)
		{
			return MessageType.FAIL;
		}
		return MessageType.SUCCESS;
		
	}
	
	public InputStream getAvatar(int Id)
	{
		byte[] b=new byte[0];
		InputStream    in = null;
		try
		{
			Connection conNew = DButil.connect();
			ResultSet rs;
			String sql2="select avatar from user_table where id=?";
			PreparedStatement ps = conNew.prepareStatement(sql2);
			ps.setInt(1,Id);
			rs=ps.executeQuery();
			rs.next();    //将光标指向第一行
			in=rs.getBinaryStream("avatar");
			/*
			b=new byte[in.available()];    //新建保存图片数据的byte数组
			in.read(b);
			OutputStream out=new FileOutputStream("D:/inspiration/OldBookServer/"+bookId+".jpg");
			out.write(b);
			out.flush();
			out.close();
			*/
			DButil.close(conNew);
			return in;
		}
		catch(SQLException e)
		{
			return in;
		}
		
	}
	
	/**
	 * 获取书籍列表
	 * @return 书籍列表
	 */
	@Override
	public String bookList()
	{
		String res="";
		Connection con = DButil.connect();
		try
		{
			String sql = "select * from book_table";
			PreparedStatement ps = con.prepareStatement(sql);
			//ps.setInt(1,36);
			ResultSet rs = ps.executeQuery();
			//rs.next();
			
			while(rs.next())
			{
				int size=0;
				if(rs.getBinaryStream("bookSurface")!=null)
				{
					size=rs.getBinaryStream("bookSurface").available();
				}
				res+=String.valueOf(rs.getInt("bookId")+"_"+
						rs.getString("bookName")+"_"+rs.getString("bookAuthor")+
						"_"+rs.getString("bookPublishing")+"_"+rs.getString("bookNumber")+
						"_"+rs.getInt("bookOwner")+"_"+size+" ");
			}
			if(res.equals(""))
				res="null";
			return res;
		}
		catch (Exception e)
		{
			 e.printStackTrace();
			 System.out.println(e.toString());
		} 
		finally
		{
			DButil.close(con);
		}
		return "null";
		
	}
	
	public String Borrow(BorrowEntity boe)
	{
		int id;
		Connection con = DButil.connect();			
		try
		{
			//获取用户总数
			ResultSet resultCount;
			String sql = "insert into book_borrow (bookName,fromUser,getUser,borrowTime,sReturnTime,returnTime,statue,evaluation) values(?,?,?,?,?,?,?,?)";   //更改数据库
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, boe.getBookName());
			ps.setInt(2, boe.getFromUser());
			ps.setInt(3, boe.getGetUser());
			ps.setString(4, boe.getBorrowTime());
			ps.setString(5,boe.getSReturnTime());
			ps.setString(6, boe.getReturnTime());
			ps.setString(7,boe.getStatue().toString());
			ps.setInt(8, boe.getEvaluation());
			int r = ps.executeUpdate();
			
			if (r > 0)
			{
				return "1";
			}
			else
				return "-1";
		}
		catch(SQLException e)
		{
			System.out.println(e.toString());
		}
		finally
		{
			DButil.close(con);
		}
		return "-1";
	}
	
	public String Return(String bookName)
	{
		
		Connection con = DButil.connect();
		try
		{
			String sql = "update book_borrow set statue=? where bookName=?";   //更改数据库
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, "WAIT_EVALUATION");
			ps.setString(2,bookName);
			ps.executeUpdate();
            DButil.close(con);
		}
		catch(SQLException e)
		{
			System.out.println(e);
			return "-1";
		}
		return "1";
	}

	public String Evaluation(String bookName,int evaluation)
	{
		Connection con = DButil.connect();
		try
		{
			String sql = "update book_borrow set statue=?,evaluation=? where bookName=?";   //更改数据库
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, "EVALUATION_OVER");
			ps.setInt(2, evaluation);
			ps.setString(3,bookName);
			
			ps.executeUpdate();
            DButil.close(con);
		}
		catch(SQLException e)
		{
			System.out.println(e);
			return "-1";
		}
		return "1";
	}
	
	public String BorrowList(int id)
	{
		String res="";
		Connection con = DButil.connect();
		try
		{
			String sql = "select * from book_borrow where fromUser=?";
		
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1,id);
			ResultSet rs = ps.executeQuery();
			//rs.next();
			while(rs.next())
			{
				
				res+=String.valueOf(rs.getString("bookName")+"+"+0+"+"+
						rs.getInt("getUser")+"+"+rs.getString("sReturnTime")+
						"+"+rs.getString("statue")+"+"+rs.getInt("evaluation")+
						" ");
			}
			if(res.equals(""))
				res="null";
			return res;
		}
		catch (Exception e)
		{
			 e.printStackTrace();
			 System.out.println(e.toString());
		} 
		finally
		{
			DButil.close(con);
		}
		return res;
	}
	public String LendList(int id)
	{
		String res="";
		Connection con = DButil.connect();
		try
		{
			String sql = "select * from book_borrow where getUser=?";
		
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1,id);
			ResultSet rs = ps.executeQuery();
			//rs.next();
			while(rs.next())
			{
				
				res+=String.valueOf(rs.getString("bookName")+"+"+
						rs.getInt("fromUser")+"+"+0+"+"+rs.getString("sReturnTime")+
						"+"+rs.getString("statue")+"+"+rs.getInt("evaluation")+
						" ");
			}
			if(res.equals(""))
				res="null";
		}
		catch (Exception e)
		{
			 e.printStackTrace();
			 System.out.println(e.toString());
		} 
		finally
		{
			DButil.close(con);
		}
		return res;
	}
	public static void main(String[] args)
	{
		UserEntity u = new UserEntity();
		UserDaoImpl dao = new UserDaoImpl();
		// u.setId(2016);
		// u.setName("qq");
		// u.setPassword("123");
		// u.setEmail("158342219@qq.com");
		// System.out.println(dao.register(u));
		// // System.out.println(dao.login(u));
		// // dao.logout(2016);
		// dao.setOnline(2016);
		// // dao.getAllId();
		//List<UserEntity> list = dao.refresh(2016);
		//System.out.println(list);

	}

}
