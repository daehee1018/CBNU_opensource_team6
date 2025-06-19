package com.example.opensource_team6.diet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.opensource_team6.databinding.FragmentDietCalcBinding

class DietCalcFragment : Fragment() {
    private var _binding: FragmentDietCalcBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DietViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDietCalcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ArrayAdapter.createFromResource(
            requireContext(),
            com.example.opensource_team6.R.array.activity_level_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerActivity.adapter = adapter
        }

        viewModel.userInfo.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.textUserInfo.text = "${it.name} | 키 ${it.height}cm, 현재 ${it.currentWeight}kg / 목표 ${it.targetWeight}kg, ${it.age}세"
            }
        }

        binding.buttonCalc.setOnClickListener {
            val period = binding.editGoalPeriod.text.toString().toIntOrNull()
            if (period == null || period <= 0) {
                Toast.makeText(requireContext(), "목표 기간을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val item = binding.spinnerActivity.selectedItem.toString()
            val level = item.substringAfter('(').substringBefore(')').toDoubleOrNull() ?: 1.2
            val result = viewModel.calculate(period, level)
            if (result == null) {
                Toast.makeText(requireContext(), "사용자 정보를 불러오지 못했습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.textCalories.text = "하루 권장: ${result.dailyCalories} kcal"
            binding.textCarb.text = "탄수화물: ${result.carbKcal} kcal / ${result.carbGram} g"
            binding.textProtein.text = "단백질: ${result.proteinKcal} kcal / ${result.proteinGram} g"
            binding.textFat.text = "지방: ${result.fatKcal} kcal / ${result.fatGram} g"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
