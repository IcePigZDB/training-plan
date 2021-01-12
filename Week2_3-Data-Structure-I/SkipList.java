package com.github.dataStructure;

import java.util.Random;

public class SkipList {

    /**
     * ÿһ����������
     */
   static class Entry {
        Entry right;
        Entry left;
        Entry up;
        Entry down;
        int key;
		public int pos;//����������
        
        public Entry(int key) {
            this.key = key;
        }
    }
    /**
     * ����ṹ
     */
    private int MAX_LEVEL;//��ǰ��߸߶� 
    private int nodes;//�������
    private Entry head;//ͷ���
    private Entry tail;//β���
    private Random random;//������������õ��ĸ���ֵ 

    public SkipList() {
        this.head = new Entry(Integer.MIN_VALUE);
        this.tail = new Entry(Integer.MAX_VALUE);
        head.right = tail;
        tail.left = head;
        this.random = new Random();
        this.nodes = 0;
        this.MAX_LEVEL = 0;
        
    }

    /**
     * ����һ��targetȥ�����������Ƿ������ֵ
     */
    public boolean search(int target) {
        Entry p;
        p=findEntry(target);
        if(p.key==target)
        	{
        	   System.out.println("����"+target+"�ɹ�");
        	   return true;
        	}
        else
        	{
        	   System.out.println("����"+target+"ʧ��");
        	   return false;
        	}
    }
    /**
     * ������Ŀ��������Ľ��
     */
    public Entry findEntry(int target) {
    	Entry p;
    	p=head;//��ͷ��ʼ����  
    	while(true) {
    		while(p.right.key!=Integer.MAX_VALUE&&p.right.key<=target)//��ֹp�ߵ�β���
    			p = p.right;//��ǰ��
    		if(p.down!=null)
    			p = p.down;
    		else 
    			break;
    	}
    	return p;
    }
    /**
     * ��һ��Ԫ�ؼ���������
     */
    public boolean add(int num) {
        Entry p,q;
        int NOW_LEVEL = 0;//�½��Ĳ���
        p = findEntry(num);//���Ҳ���λ��
        //�����Ծ���д��ں���numֵ�Ľ�㣬��ֱ�ӷ���
        if(p.key==num)
        	return true;
        //�����Ծ���в����ں�numֵ�Ľ�㣬�������������
        q = new Entry(num);
        q.left = p;
        q.right = p.right;
        p.right.left = q;
        p.right = q;
        //��Ӳ����������Ƿ��ϲ����  
        while(random.nextDouble()<0.5) {
        	if(NOW_LEVEL==MAX_LEVEL)//�ѵ�����߲㣬������һ��
        		addEmptyLevel();
        	while(p.up==null)
        		p = p.left;
        	p = p.up;
        	//ע�⣺���ײ���֮��Ľ�������Ҫvalueֵ
        	Entry s;
        	s = new Entry(num);
        	s.left = p;
        	s.right = p.right;
        	s.down = q;
        	p.right.left = s;
        	p.right = s;
        	q.up = s;
        	q = s;//qָ���½��s
        	NOW_LEVEL = NOW_LEVEL + 1;//�½��������� 
        }
        nodes = nodes + 1;//���������� 
        System.out.println("��"+num+"�����ĸ߶�Ϊ"+(NOW_LEVEL+1));
        System.out.println("Ŀǰ�������Ϊ"+nodes);
        return true;
        	
        }
    //����һ��
    private void addEmptyLevel() {
    	Entry p1,p2;
    	p1 = new Entry(Integer.MIN_VALUE);
        p2 = new Entry(Integer.MAX_VALUE);
        p1.right = p2;
        p1.down = head;
        p2.left = p1;
        p2.down = tail;
        head.up = p1;
        tail.up = p2;
        head = p1;
        tail = p2;
        MAX_LEVEL = MAX_LEVEL + 1;
        
    }

    /**
     * �������е�ĳһ��Ԫ��ɾ��
     */
    public boolean erase(int num) {
        Entry p,q;
        p = findEntry(num);
        if(p.key!=num) //������Ŀ����
            return false;
        while(p!=null) {
        	q = p.up;
        	p.left.right = p.right;
        	p.right.left = p.left;
        	p = q;
        }
        	
        	return true;
        	
        }
    /**
     * ��ӡ����
     */
    public void printHorizontal()
	  {
	     String s = "";
	     int i;
   	     Entry p;

	    //��p�ƶ����ײ�
	     p = head;

	     while ( p.down != null )
	     {
	        p = p.down;
	     }
        //���ײ�ÿ������ţ������ӡ�հ׵Ľ��
	     i = 0;
	     while ( p != null )
	     {
	        p.pos = i++;
	        p = p.right;
	     }

	    //��ӡ
	     p = head;

	     while ( p != null )
	     {
	        s = getOneRow( p );
		    System.out.println(s);

	        p = p.down;
	     }
	  }
      //���ÿһ��
	  public String getOneRow(Entry p )
	  {
	     String s;
	     int a, b, i;

	     a = 0;
        //�����һ������ֵ
	     s = "" + p.key;
	     
	     p = p.right;


	     while ( p != null )
	     {
	        Entry q;
	        q = p;
	        while (q.down != null) 
	        {
		        q = q.down;
	        }
	        b = q.pos;

	        s = s + " <-";

            //������pǰ�հ׽��
	        for (i = a+1; i < b; i++)
	           s = s + "--------";
	        //���p����ֵ
	        s = s + "> " + p.key;

	        a = b;

	        p = p.right;
	     }

	     return(s);
	  }
        	
    }

	
    
    
