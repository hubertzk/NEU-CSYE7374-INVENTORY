package edu.neu.csye7374.GUI;

import edu.neu.csye7374.Inventory.Inventory;
import edu.neu.csye7374.Inventory.Item;
import edu.neu.csye7374.Inventory.ItemAPI;
import edu.neu.csye7374.Inventory.ItemFactory;
import edu.neu.csye7374.Observer.AnnualReview;
import edu.neu.csye7374.Observer.TodayDate;
import edu.neu.csye7374.Observer.TrackExp;
import edu.neu.csye7374.Order.Invoice;
import edu.neu.csye7374.Order.InvoiceFactory;
import edu.neu.csye7374.Order.Order;
import edu.neu.csye7374.Personnel.Employee;
import edu.neu.csye7374.Personnel.PersonFactory;
import edu.neu.csye7374.Personnel.Personnel;

import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;



public class MainFrame extends JFrame {

    private JPanel MainContainer;
    private JPanel InventoryPanel;
    private JPanel PersonnelPanel;
    private JPanel OrderPanel;
    private JPanel MainPanel;
    private JButton inventoryButton;
    private JButton personnelButton;
    private JButton orderButton;
    private JButton deleteButtonItem;
    private JButton alertButtonItem;
    private JButton backButtonItem;
    private JButton backButtonPerson;
    private JButton backButtonOrder;
    private JList<String> inventoryList;
    private JTextField itemID;
    private JTextField itemName;
    private JTextField itemPD;
    private JTextField itemExp;
    private JTextField itemPrice;
    private JTextField itemLocation;
    private JTextField itemDestination;
    private JButton createButtonItem;
    private JButton saveButtonItem;
    private JLabel status;
    private JButton newButtonItem;
    private JButton deleteButtonEmp;
    private JButton alertButtonEmp;
    private JButton newButtonEmp;
    private JList<String> employeeList;
    private JButton createButtonEmp;
    private JButton saveButtonEmp;
    private JTextField empID;
    private JTextField empAge;
    private JTextField empName;
    private JTextField empWage;
    private JTextField empHireDate;
    private JLabel empStatus;
    // data related fields
    private DefaultListModel<String> itemListModel;
    private Inventory inventory;
    private ItemFactory itemFactory;
    private List<ItemAPI> items;
    private List<ItemAPI> expItems;
    private boolean itemAlert = false;
    private DefaultListModel<String> employeeListModel;
    private Personnel personnel;
    private PersonFactory personFactory;
    private List<Employee> employees;
    private List<Employee> expEmployees;
    private boolean employeeAlert = false;
    private TodayDate tdate;
    private TrackExp trackExp;
    private AnnualReview annualReview;
    private DefaultListModel invoiceListModel;
    private Order order;
    private InvoiceFactory invoiceFactory;
    private List<Invoice> invoices;
    private final String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";


    public MainFrame() {
        // initialize program factories
        // item
        inventory = Inventory.getInstance();
        itemFactory = ItemFactory.getInstance();
        itemListModel = new DefaultListModel<>();
        inventoryList.setModel(itemListModel);
        initItemList();
        // personnel
        personnel = Personnel.getInstance();
        personFactory = PersonFactory.getInstance();
        employeeListModel = new DefaultListModel<>();
        employeeList.setModel(employeeListModel);
        initEmpList();

        // alert
        tdate = new TodayDate();
        trackExp = new TrackExp();
        annualReview = new AnnualReview();
        tdate.register(trackExp);
        tdate.register(annualReview);
        tdate.setDate(LocalDate.now().toString());
        expItems = trackExp.getAllExpItem();
        expEmployees = annualReview.alertAnnualReview();

        // order
        order = Order.getInstance();
        invoiceFactory = InvoiceFactory.getInstance();

        // set frame
        setContentPane(MainContainer);
        setTitle("Business Management Tool");
        setSize(1000, 750);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);




        //==================== GUI Listeners ============================/
        inventoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout)(MainContainer.getLayout());
                cl.show(MainContainer, "inventory");

            }
        });
        personnelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout)(MainContainer.getLayout());
                cl.show(MainContainer, "personnel");
            }
        });
        orderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout)(MainContainer.getLayout());
                cl.show(MainContainer, "order");
            }
        });
        backButtonItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                back();
            }
        });
        backButtonPerson.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                back();
            }
        });
        backButtonOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                back();
            }
        });
        inventoryList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                String val = inventoryList.getSelectedValue();
                if(val != null){
                    String[] temp = val.split(": ");
                    int itemID = Integer.parseInt(temp[0]);
                    ItemAPI item = inventory.getItem(itemID);
                    populateItem(item);
                    deleteButtonItem.setEnabled(true);
                    saveButtonItem.setEnabled(true);
                    createButtonItem.setEnabled(false);
                }else{
                    clearItemFields();
                    deleteButtonItem.setEnabled(false);
                    saveButtonItem.setEnabled(false);
                    createButtonItem.setEnabled(true);
                }
            }
        });
        deleteButtonItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String val = inventoryList.getSelectedValue();
                int idx = inventoryList.getSelectedIndex();
                if(val != null){
                    String[] temp = val.split(": ");
                    int itemID = Integer.parseInt(temp[0]);
                    inventory.deleteItem(itemID);
                    clearItemFields();
                    itemListModel.remove(idx);
                    status.setText("Delete Success!");
                }
            }
        });
        saveButtonItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ItemAPI item = validateItem(false);
                if(item != null && item.getId() != -1){
                    int id = item.getId();
                    inventory.updateItem(id, item);
                    initItemList();
                    status.setText("Update Success!");
                }
            }
        });
        createButtonItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ItemAPI item = validateItem(true);
                if(item != null && item.getId() != -1){
                    inventory.appendItem(item);
                    initItemList();
                    status.setText("Create Success!");
                }
            }
        });
        newButtonItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inventoryList.clearSelection();
            }
        });
        inventoryList.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
               Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
               if(value != null && itemAlert){
                   boolean exped = false;
                   String[] temp = value.toString().split(": ");
                   int itemID = Integer.parseInt(temp[0]);
                   for(ItemAPI expItem : expItems){
                       if(expItem.getId() == itemID){
                           exped = true;
                           break;
                       }
                   }
                   if(exped){
                       c.setBackground(Color.RED);
                   }
               }

               return c;
            }
        });
        alertButtonItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expItems = trackExp.getAllExpItem();
                itemAlert = !itemAlert;
                inventoryList.updateUI();
                if(itemAlert){
                    status.setText("Today's date: " + LocalDate.now().toString());
                }else{
                    status.setText("Result of your actions");
                }
            }
        });
        employeeList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                String val = employeeList.getSelectedValue();
                if(val != null){
                    String[] temp = val.split(": ");
                    int employeeID = Integer.parseInt(temp[0]);
                    Employee employee = personnel.getEmployee(employeeID);
                    populateEmployee(employee);
                    deleteButtonEmp.setEnabled(true);
                    saveButtonEmp.setEnabled(true);
                    createButtonEmp.setEnabled(false);
                }else{
                    clearEmployeeFields();
                    deleteButtonEmp.setEnabled(false);
                    saveButtonEmp.setEnabled(false);
                    createButtonEmp.setEnabled(true);
                }
            }
        });
        deleteButtonEmp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String val = employeeList.getSelectedValue();
                int idx = employeeList.getSelectedIndex();
                if(val != null){
                    String[] temp = val.split(": ");
                    int employeeID = Integer.parseInt(temp[0]);
                    personnel.deleteEmployee(employeeID);
                    clearEmployeeFields();
                    employeeListModel.remove(idx);
                    empStatus.setText("Delete Success!");
                }
            }
        });
        saveButtonEmp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Employee employee = validateEmployee(false);
                if(employee != null && employee.getId() != -1){
                    int id = employee.getId();
                    personnel.updateEmployee(id, employee);
                    initEmpList();
                    empStatus.setText("Update Success!");
                }
            }
        });
        createButtonEmp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Employee employee = validateEmployee(true);
                if(employee != null && employee.getId() != -1){
                    personnel.appendEmployee(employee);
                    initEmpList();
                    empStatus.setText("Create Success!");
                }
            }
        });
        newButtonEmp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                employeeList.clearSelection();
            }
        });
        employeeList.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value != null && employeeAlert){
                    boolean exped = false;
                    String[] temp = value.toString().split(": ");
                    int employeeID = Integer.parseInt(temp[0]);
                    for(Employee expEmployee : expEmployees){
                        if(expEmployee.getId() == employeeID){
                            exped = true;
                            break;
                        }
                    }
                    if(exped){
                        c.setBackground(Color.RED);
                    }
                }

                return c;
            }
        });
        alertButtonEmp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expEmployees = annualReview.alertAnnualReview();
                employeeAlert = !employeeAlert;
                employeeList.updateUI();
                if(employeeAlert){
                    empStatus.setText("Today's date: " + LocalDate.now().toString());
                }else{
                    empStatus.setText("Result of your actions");
                }
            }
        });
    }

    //====================== helper functions =======================//

    private void back() {
        CardLayout cl = (CardLayout)(MainContainer.getLayout());
        cl.show(MainContainer, "main");
    }

    private void initItemList() {
        itemListModel.removeAllElements();
        items = inventory.getItemAll();
        for(ItemAPI item : items) {
            System.out.println("adding" + item.getName());
            itemListModel.addElement(item.getId() + ": " + item.getName());
        }
    }

    private void populateItem(ItemAPI item){
        itemID.setText(String.valueOf(item.getId()));
        itemName.setText(item.getName());
        itemPD.setText(item.getPD());
        itemExp.setText(item.getExp());
        itemPrice.setText(String.valueOf(item.getPrice()));
        String[] locs = item.getLocation().split(" -- ");
        itemLocation.setText(locs[0]);
        if(locs.length > 1){
            itemDestination.setText(locs[1]);
        }else{
            itemDestination.setText("");
        }
    }

    private ItemAPI validateItem(boolean ifCreate){
        try {
            int id = Integer.parseInt(itemID.getText());
            String name = itemName.getText();
            String PD = itemPD.getText();
            boolean pdCheck = Pattern.compile(datePattern).matcher(PD).matches();
            if(!pdCheck) throw new Exception("PD Date format wrong");
            String Exp = itemExp.getText();
            boolean expCheck = Pattern.compile(datePattern).matcher(Exp).matches();
            if(!expCheck) throw new Exception("Exp Date format wrong");
            double price = Double.parseDouble(itemPrice.getText());
            String location = itemLocation.getText();
            String destination = itemDestination.getText();
            if(destination != "") location = location + " -- " + destination;
            // differentiate editing and creating
            if(ifCreate){
                return itemFactory.produceItem(id, name, PD, Exp, price, location);
            }else {
                ItemAPI item = new Item();
                item.setId(id);
                item.setName(name);
                item.setPD(PD);
                item.setExp(Exp);
                item.setPrice(price);
                item.setLocation(location);
                return item;
            }

        }catch(Exception e){
            status.setText(e.getMessage());
            System.out.println("Error!" + e.getMessage());
            return null;
        }
    }

    private void clearItemFields(){
        itemID.setText("");
        itemName.setText("");
        itemPD.setText("");
        itemExp.setText("");
        itemPrice.setText("");
        itemDestination.setText("");
        itemLocation.setText("");
    }

    private void initEmpList() {
        employeeListModel.removeAllElements();
        employees = personnel.getAllEmployee();
        for(Employee employee : employees) {
            System.out.println("adding" + employee.getName());
            employeeListModel.addElement(employee.getId() + ": " + employee.getName());
        }
    }

    private void populateEmployee(Employee employee) {
        empID.setText(String.valueOf(employee.getId()));
        empAge.setText(String.valueOf(employee.getAge()));
        empName.setText(employee.getName());
        empWage.setText(String.valueOf(employee.getWage()));
        empHireDate.setText(employee.getHireDate());
    }

    private Employee validateEmployee(boolean ifCreate){
        try {
            int id = Integer.parseInt(empID.getText());
            int age = Integer.parseInt(empAge.getText());
            String name = empName.getText();
            double wage = Double.parseDouble(empWage.getText());
            String hireDate = empHireDate.getText();
            boolean hdCheck = Pattern.compile(datePattern).matcher(hireDate).matches();
            if(!hdCheck) throw new Exception("Hire Date format wrong");
            // differentiate editing and creating
            if(ifCreate){
                return personFactory.produceEmployee(id, age, name, wage, hireDate);
            }else {
                Employee employee = new Employee();
                employee.setId(id);
                employee.setName(name);
                employee.setAge(age);
                employee.setWage(wage);
                employee.setHireDate(hireDate);
                return employee;
            }

        }catch(Exception e){
            empStatus.setText(e.getMessage());
            System.out.println("Error!" + e.getMessage());
            return null;
        }
    }

    private void clearEmployeeFields(){
        empID.setText("");
        empName.setText("");
        empAge.setText("");
        empWage.setText("");
        empHireDate.setText("");
    }
}
