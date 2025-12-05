import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shing.recordkeep.service.StudentService;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/sections/{sectionId}/students/{lrn}/update")
    public String updateStudent(@PathVariable String lrn,
                                @PathVariable Long sectionId,
                                @RequestParam String firstName,
                                @RequestParam String surname,
                                @RequestParam String sex,
                                RedirectAttributes redirectAttributes) {
        try {
            studentService.updateStudent(lrn, firstName, surname, sex);
            redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating student: " + e.getMessage());
        }
        return "redirect:/sections/" + sectionId + "/students";
    }

    @PostMapping("/sections/{sectionId}/students/{lrn}/delete")
    public String deleteStudent(@PathVariable String lrn,
                                @PathVariable Long sectionId,
                                RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(lrn);
            redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting student: " + e.getMessage());
        }
        return "redirect:/sections/" + sectionId + "/students";
    }
}
